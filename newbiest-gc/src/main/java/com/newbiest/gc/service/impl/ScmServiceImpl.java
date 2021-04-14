package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.*;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.scm.send.mlot.state.MaterialLotStateReportRequest;
import com.newbiest.gc.scm.send.mlot.state.MaterialLotStateReportRequestBody;
import com.newbiest.gc.service.ScmService;
import com.newbiest.gc.service.model.QueryEngResponse;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotHistory;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.msg.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.http.impl.client.HttpClientBuilder.create;

/**
 * 请求GC的SCM系统
 * API地址：https://www.showdoc.cc/949149104775520?page_id=4964217376034753 密码123456
 * mscm地址查看文档
 * @author guoxunbo
 * @date 2020-08-09 10:40
 */
@Service
@Slf4j
@Data
@EnableAsync
public class ScmServiceImpl implements ScmService {

    /**
     * 连接SCM的超时时间 单位秒
     */
    public static final int SCM_CONNECTION_TIME_OUT = 30;

    /**
     * 读取SCM的超时时间 单位秒
     */
    public static final int SCM_READ_TIME_OUT = 60;

    public static final String SCM_TAG_TABLE_NAEM = "GCCPStockOutTagging";
    public static final String SCM_UNTAG_TABLE_NAEM = "GCWaferUnStockOutTagging";

    public static final String REFERENCE_NAME_FOR_SCM = "SCMImportType";
    public static final String QUERY_ENG_API = "/api/wip/sync-eng/query";

    public static final String MATERIAL_LOT_STATE_REPORT = "/api/wip/wipdata/update_stage?system_id=1";

    public static final String MSCM_SERVICE_NAME = "interface";
    public static final String MSCM_TOKEN_API = "/api/?r=Api/Token/AccessToken";
    public static final String MSCM_ADD_TRACKING_API = "/api/?r=Api/Logistics/AddTracking";

    private RestTemplate restTemplate;

    @Value("${gc.scmUrl}")
    private String scmUrl;

    @Value("${gc.mScmUrl}")
    private String mScmUrl;

    @Value("${gc.mScmUsername}")
    private String mScmUsername;

    @Value("${gc.mScmPassword}")
    private String mScmPassword;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;


    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    UIService uiService;

    @Autowired
    MmsService mmsService;

    @Autowired
    BaseService baseService;

    @Autowired
    PackageService packageService;

    @PostConstruct
    public void init() {
        CloseableHttpClient client = createHttpClient().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        requestFactory.setConnectTimeout(SCM_CONNECTION_TIME_OUT * 1000);
        requestFactory.setReadTimeout(SCM_READ_TIME_OUT * 1000);
        restTemplate = new RestTemplate(requestFactory);
    }

    protected HttpClientBuilder createHttpClient() {
        return create().useSystemProperties().disableRedirectHandling().disableCookieManagement();
    }

    public void scmAssign(String lotId, String vendor, String poId, String materialType, String remarks) throws ClientException{
        try {
            MaterialLot materialLot = getMaterialLotByNbTableNameAndLotId(SCM_TAG_TABLE_NAEM, lotId);

            scmAssignAndSaveHis(materialLot, materialType, vendor, poId, remarks);

            //如果LOT已经装箱，验证箱中所有的LOT是否已经标注，如果全部标注，对箱号进行标注(箱中LOT的标注信息保持一致)
            if(!StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                MaterialLot parentMaterialLot = mmsService.getMLotByMLotId(materialLot.getParentMaterialLotId(), true);
                validateParentMLotScmAssign(parentMaterialLot, materialType, vendor, poId, remarks);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据表单名称和lotId查询需要标注和取消标注的lot信息
     * @param tableName
     * @param lotId
     * @return
     * @throws ClientException
     */
    private MaterialLot getMaterialLotByNbTableNameAndLotId(String tableName, String lotId) throws ClientException{
        try {
            MaterialLot materialLot = new MaterialLot();
            NBTable nbTable = uiService.getNBTableByName(tableName);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer();
            clauseBuffer.append(" lotId = ");
            clauseBuffer.append("'" + lotId + "'");
            if (!StringUtils.isNullOrEmpty(whereClause)) {
                clauseBuffer.append(" AND ");
                clauseBuffer.append(whereClause);
            }
            whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);

            if (CollectionUtils.isEmpty(materialLots)) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, lotId);
            } else {
                materialLot = materialLots.get(0);
            }
            return materialLot;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证箱中Lot是否全部标注，标注信息是否一致
     * @param parentMaterialLot
     * @param materialType
     * @param vendor
     * @param poId
     * @param remarks
     * @throws ClientException
     */
    private void validateParentMLotScmAssign(MaterialLot parentMaterialLot, String materialType, String vendor, String poId, String remarks) throws ClientException{
        try {
            List<MaterialLot> materialLotList = packageService.getPackageDetailLots(parentMaterialLot.getObjectRrn());
            List<MaterialLot> unTagMaterialLotList = materialLotList.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getReserved54())).collect(Collectors.toList());

            if(CollectionUtils.isEmpty(unTagMaterialLotList)){
                Map<String, List<MaterialLot>> mLotAssignMap =  materialLotList.stream().collect(Collectors.groupingBy(mLot -> {
                    StringBuffer key = new StringBuffer();
                    if(StringUtils.isNullOrEmpty(mLot.getReserved56())){
                        key.append(StringUtils.EMPTY);
                    } else {
                        key.append(mLot.getReserved56());
                    }
                    if(StringUtils.isNullOrEmpty(mLot.getReserved54())){
                        key.append(StringUtils.EMPTY);
                    } else {
                        key.append(mLot.getReserved54());
                    }
                    return key.toString();
                }));
                if (mLotAssignMap != null &&  mLotAssignMap.size() > 1) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_TAG_INFO_IS_NOT_SAME, parentMaterialLot.getMaterialLotId());
                }
                scmAssignAndSaveHis(parentMaterialLot, materialType, vendor, poId, remarks);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * scm跨域标注，记录标注信息，并保存历史
     * @param materialLot
     * @param materialType
     * @param vendor
     * @param poId
     * @param remarks
     * @throws ClientException
     */
    private void scmAssignAndSaveHis(MaterialLot materialLot, String materialType, String vendor, String poId, String remarks) throws ClientException{
        try {
            materialLot.setReserved54(materialType);
            materialLot.setReserved55(vendor);
            materialLot.setReserved56(poId);
            materialLot.setReserved57(remarks);
            materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, "SCMAssign");
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void scmUnAssign(String lotId) throws ClientException{
        try {
            MaterialLot materialLot = getMaterialLotByNbTableNameAndLotId(SCM_UNTAG_TABLE_NAEM, lotId);

            scmUnAssignMaterialLot(materialLot);

            if(!StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                MaterialLot parentMLot = mmsService.getMLotByMLotId(materialLot.getParentMaterialLotId(), true);
                scmUnAssignMaterialLot(parentMLot);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * SAM取消晶圆标注
     * @param materialLot
     * @throws ClientException
     */
    private void scmUnAssignMaterialLot(MaterialLot materialLot) throws ClientException{
        try {
            materialLot.setReserved54(StringUtils.EMPTY);
            materialLot.setReserved55(StringUtils.EMPTY);
            materialLot.setReserved56(StringUtils.EMPTY);
            materialLot.setReserved57(StringUtils.EMPTY);
            materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, "SCMUnAssign");
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据标记调用SCM系统进行判断是否是ENG产品
     * @param materialLotUnits
     * @throws ClientException
     */
    public List<MaterialLotUnit> assignEngFlag(List<MaterialLotUnit> materialLotUnits) throws ClientException {
        try {
            List<NBOwnerReferenceList> connectScmImportTypeList = getImportTypeForScm();
            if (CollectionUtils.isEmpty(connectScmImportTypeList)) {
                log.warn("OwnerRefList SCMImportType is not config. so does not connect to scm");
                return materialLotUnits;
            }
            List<Map> requestWaferList = Lists.newArrayList();
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                Map<String, String> requestInfo = Maps.newHashMap();
                if (StringUtils.isNullOrEmpty(materialLotUnit.getReserved30())) {
                    log.warn(String.format("MaterialUnit [%s] has no lotId.", materialLotUnit.getUnitId()));
                    continue;
                }
                // 验证导入类型是否在要发给SCM的类型中
                Optional optional = connectScmImportTypeList.stream().filter(connectScmImportType -> connectScmImportType.getKey().equals(materialLotUnit.getReserved49())).findFirst();
                if (!optional.isPresent()) {
                    log.warn(String.format("MaterialLotUnit [%s]'s import type is not in SCMImportType so skip it", materialLotUnit.getUnitId()));
                    continue;
                }

                Integer waferId = Integer.parseInt(materialLotUnit.getReserved31());
                requestInfo.put("lot_no", materialLotUnit.getReserved30());
                requestInfo.put("wafer_id", waferId.toString());
                requestWaferList.add(requestInfo);
            }
            Map<String, Object> requestInfo = Maps.newHashMap();
            requestInfo.put("engs", requestWaferList);

            String response = sendHttpRequest(scmUrl + QUERY_ENG_API, requestInfo, Maps.newHashMap());
            QueryEngResponse queryEngResponse = DefaultParser.getObjectMapper().readerFor(QueryEngResponse.class).readValue(response);
            if (!QueryEngResponse.SUCCESS_CODE.equals(queryEngResponse.getCode())) {
                throw new ClientException(queryEngResponse.getMessage());
            }
            List<Map> responseDataList = queryEngResponse.getData();
            List<String> engWaferIdList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(responseDataList)) {
                for (Map responseData : responseDataList) {
                    String lotId = (String) responseData.get("lot_no");
                    String waferId = (String) responseData.get("wafer_id");
                    boolean engFlag = (boolean) responseData.get("is_eng");
                    if (engFlag) {
                        String unitId = lotId + StringUtils.SPLIT_CODE + waferId;
                        for(MaterialLotUnit materialLotUnit: materialLotUnits){
                            if(unitId.equals(materialLotUnit.getUnitId())){
                                materialLotUnit.setProductType(MaterialLotUnit.PRODUCT_TYPE_ENG);
                            }
                        }
                        engWaferIdList.add(lotId + StringUtils.SPLIT_CODE + waferId);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(engWaferIdList)) {
                log.debug(String.format("Eng Wafer List is [%s]", engWaferIdList));
            }
            return materialLotUnits;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private List<NBOwnerReferenceList> getImportTypeForScm() {
        List<NBOwnerReferenceList> referenceLists = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_FOR_SCM, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(referenceLists)) {
            return referenceLists;
        }
        return Lists.newArrayList();
    }

    @Async
    public void sendMaterialStateReport(List<MaterialLot> materialLots, String action,  SessionContext sc) throws ClientException {
        try {
            MaterialLotStateReportRequest request = new MaterialLotStateReportRequest();
            RequestHeader requestHeader = new RequestHeader();
            requestHeader.setOrgName(sc.getOrgName());
            requestHeader.setOrgRrn(sc.getOrgRrn());
            requestHeader.setUsername(sc.getUsername());
            requestHeader.setTransactionId(sc.getTransRrn());
            request.setHeader(requestHeader);

            MaterialLotStateReportRequestBody requestBody = new MaterialLotStateReportRequestBody();
            List<Map<String, String>> reportDataList = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                for(MaterialLotUnit materialLotUnit: materialLotUnitList){
                    Map<String, String> reportData = Maps.newHashMap();
                    reportData.put("lotId", materialLotUnit.getLotId());
                    String waferId = materialLotUnit.getUnitId().split("-")[1];
                    reportData.put("waferId", waferId);
                    reportDataList.add(reportData);
                }
            }
            requestBody.setActionType(action);
            requestBody.setMaterialLotList(reportDataList);
            request.setBody(requestBody);
            String responseStr = sendHttpRequest(scmUrl + MATERIAL_LOT_STATE_REPORT, request, Maps.newHashMap());

            Response response = DefaultParser.getObjectMapper().readerFor(Map.class).readValue(responseStr);
            if (!ResponseHeader.RESULT_SUCCESS.equals(response.getHeader().getResult())) {
                throw new ClientException(response.getHeader().getResultCode());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private String sendHttpRequest(String url, Object requestInfo, Map<String, String> httpHeaders) throws ClientException {
        try {
            String response = StringUtils.EMPTY;
            String requestString = DefaultParser.getObjectMapper().writeValueAsString(requestInfo);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Send data. RequestString is [%s]", requestString));
            }

            HttpHeaders headers = new HttpHeaders();
            String contentType = httpHeaders.get("contentType");
            if (StringUtils.isNullOrEmpty(contentType)) {
                contentType = "application/json";
            }
            headers.put("Content-Type", Lists.newArrayList(contentType));

            String token = httpHeaders.get("authorization");
            if (!StringUtils.isNullOrEmpty(token)) {
                headers.put("authorization", Lists.newArrayList(token));
            }
            ResponseEntity<byte[]> responseEntity = null;
            if ("application/x-www-form-urlencoded".equals(contentType)) {
                MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();

                Map<String, Object> requestMap = (Map<String, Object>) requestInfo;
                for (String key : requestMap.keySet()) {
                    postParameters.add(key, requestMap.get(key));
                }
                HttpEntity<MultiValueMap> httpEntity = new HttpEntity<>(postParameters, headers);
                Map responseMap = restTemplate.postForObject(new URI(url), httpEntity, Map.class);
                response = DefaultParser.writerJson(responseMap);
            } else {
                RequestEntity request = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(url));
                responseEntity = restTemplate.exchange(request, byte[].class);
                response = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());

            }
            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response by scm. Response is [%s]", response));
            }
            return response;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private String getReceivingTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
        formatter.setLenient(false);
        return formatter.format(DateUtils.now());
    }

    public void addTracking(String orderId, String expressNumber, boolean isKuayueExprress) throws ClientException{
        try {
            String token = getMScmToken();
            Map httpHeader = Maps.newHashMap();
            httpHeader.put("authorization", token);

            List<Map> requestInfoList = Lists.newArrayList();
            Map requestInfo = Maps.newHashMap();
            requestInfo.put("send_code", orderId);
            if (isKuayueExprress) {
                requestInfo.put("logistics_receiving_time", getReceivingTime());
                requestInfo.put("logistics_company_name", "跨越物流");
                requestInfo.put("logistics_code", expressNumber);
            }
            requestInfoList.add(requestInfo);

            String response = sendHttpRequest(mScmUrl + MSCM_ADD_TRACKING_API, requestInfoList, httpHeader);
            Map<String, Object> responseData = DefaultParser.getObjectMapper().readerFor(Map.class).readValue(response);
            Integer ret = (Integer) responseData.get("ret");
            if (200 != ret) {
                throw new ClientParameterException(GcExceptions.MSCM_ERROR, responseData.get("msg"));
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次分组处理，(手动下单、自动下单、没有记录快递单号)
     * 一个快递单号访问一次
     * @param orderId
     * @param materialLotList
     * @throws ClientException
     */
    @Async
    public void addScmTracking(String orderId, List<MaterialLot> materialLotList) throws ClientException{
        try {
            List<Map> requestInfoList = Lists.newArrayList();
            List<MaterialLot> autoOrderMLots = materialLotList.stream().filter(materialLot -> MaterialLot.PLAN_ORDER_TYPE_AUTO.equals(materialLot.getPlanOrderType())).collect(Collectors.toList());
            List<MaterialLot> manualOrderMLots = materialLotList.stream().filter(materialLot -> MaterialLot.PLAN_ORDER_TYPE_MANUAL.equals(materialLot.getPlanOrderType())).collect(Collectors.toList());
            List<MaterialLot> unOrderMLots = materialLotList.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getExpressNumber())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(autoOrderMLots)){
                Map<String, List<MaterialLot>> autoOrderMLotMap = autoOrderMLots.stream().collect(Collectors.groupingBy(MaterialLot :: getExpressNumber));
                for(String expressNumber: autoOrderMLotMap.keySet()){
                    Map requestInfo = Maps.newHashMap();
                    requestInfo.put("send_code", orderId);
                    requestInfo.put("logistics_receiving_time", getReceivingTime());
                    requestInfo.put("logistics_company_name", "跨越物流");
                    requestInfo.put("logistics_code", expressNumber);
                    requestInfoList.add(requestInfo);
                }
            }
            if(CollectionUtils.isNotEmpty(manualOrderMLots)){
                Map<String, List<MaterialLot>> manualOrderMLotMap = manualOrderMLots.stream().collect(Collectors.groupingBy(MaterialLot :: getExpressNumber));
                for(String expressNumber : manualOrderMLotMap.keySet()){
                    List<MaterialLot> materialLots = manualOrderMLotMap.get(expressNumber);
                    String expressCompany = materialLots.get(0).getExpressCompany();
                    Map requestInfo = Maps.newHashMap();
                    requestInfo.put("send_code", orderId);
                    requestInfo.put("logistics_receiving_time", getReceivingTime());
                    requestInfo.put("logistics_company_name", expressCompany);
                    requestInfo.put("logistics_code", expressNumber);
                    requestInfoList.add(requestInfo);
                }
            }
            if(CollectionUtils.isNotEmpty(unOrderMLots)){
                Map requestInfo = Maps.newHashMap();
                requestInfo.put("send_code", orderId);
                requestInfo.put("logistics_receiving_time", getReceivingTime());
                requestInfo.put("logistics_company_name", "");
                requestInfo.put("logistics_code", "");
                requestInfoList.add(requestInfo);
            }

            String token = getMScmToken();
            Map httpHeader = Maps.newHashMap();
            httpHeader.put("authorization", token);

            String response = sendHttpRequest(mScmUrl + MSCM_ADD_TRACKING_API, requestInfoList, httpHeader);
            Map<String, Object> responseData = DefaultParser.getObjectMapper().readerFor(Map.class).readValue(response);
            Integer ret = (Integer) responseData.get("ret");
            if (200 != ret) {
                throw new ClientParameterException(GcExceptions.MSCM_ERROR, responseData.get("msg"));
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String getMScmToken() throws ClientException{
        try {
            Map httpHeader = Maps.newHashMap();
            httpHeader.put("contentType", "application/x-www-form-urlencoded");

            Map<String, String> requestInfo = Maps.newHashMap();
            requestInfo.put("app_name", mScmUsername);
            requestInfo.put("app_secret", mScmPassword);
            requestInfo.put("service", MSCM_SERVICE_NAME);

            String response = sendHttpRequest(mScmUrl + MSCM_TOKEN_API, requestInfo, httpHeader);
            Map<String, Object> responseData = DefaultParser.getObjectMapper().readerFor(Map.class).readValue(response);

            Integer ret = (Integer) responseData.get("ret");
            if (200 != ret) {
                throw new ClientParameterException(GcExceptions.MSCM_ERROR, responseData.get("msg"));
            }
            Map<String, Object> data = (Map<String, Object>) responseData.get("data");
            String token = (String) data.get("token");
            return token;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


}
