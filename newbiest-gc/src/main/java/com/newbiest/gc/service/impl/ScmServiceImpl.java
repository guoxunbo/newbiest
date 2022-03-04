package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.*;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.model.GCScmToMesEngInform;
import com.newbiest.gc.model.GCScmToMesEngInformHis;
import com.newbiest.gc.repository.GCScmToMesEngInformHisRepository;
import com.newbiest.gc.repository.GCScmToMesEngInformRepository;
import com.newbiest.gc.rest.scm.engManager.EngManagerRequest;
import com.newbiest.gc.service.model.QueryWaferResponse;
import com.newbiest.mms.model.FutureHoldConfig;
import com.newbiest.mms.model.FutureHoldConfigHis;
import com.newbiest.mms.repository.FutureHoldConfigHisRepository;
import com.newbiest.mms.repository.FutureHoldConfigRepository;
import com.newbiest.gc.scm.send.mlot.state.MaterialLotStateReportRequest;
import com.newbiest.gc.scm.send.mlot.state.MaterialLotStateReportRequestBody;
import com.newbiest.gc.service.ScmService;
import com.newbiest.gc.service.model.QueryEngResponse;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.utils.CollectorsUtils;
import com.newbiest.msg.*;
import io.swagger.annotations.Api;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
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
@Transactional
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

    public static final String MSCM_QUERY_WAFER_BY_WONO_API = "/api/wip/wipdata-wo/get-details";
    public static final String BATE_APP_KEY = "5128e6b9-759a-47e8-8341-d0ca552ac10b";//测试环境Key(COB晶圆非确认晶圆追踪)
    public static final String PROD_APP_KEY = "460ffd71-e795-4f7d-870b-e8c5b4a930f5";//正式环境Key
    public static final String SYSTEM_ID = "2";
    public static final String VERSION = "v1";

    public static final String SCM_RETRY_VALIDATE_MATERIAL_LOT_ENG = "https://gc-scm.wochacha.com/api/wip/sync-eng/query";

    private RestTemplate restTemplate;

    private List<String> needTokenUrlList = Lists.newArrayList();

    @Value("${gc.scmUrl}")
    private String scmUrl;

    @Value("${gc.mScmUrl}")
    private String mScmUrl;

    @Value("${gc.wScmUrl}")
    private String wScmUrl;

    @Value("${gc.mScmUsername}")
    private String mScmUsername;

    @Value("${gc.mScmPassword}")
    private String mScmPassword;

    @Value("${spring.profiles.active}")
    private String profiles;

    private boolean isProdEnv() {
        return "production".equalsIgnoreCase(profiles);
    }

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

    @Autowired
    UIService uiService;

    @Autowired
    MmsService mmsService;

    @Autowired
    BaseService baseService;

    @Autowired
    PackageService packageService;

    @Autowired
    InterfaceFailRepository interfaceFailRepository;

    @Autowired
    InterfaceHistoryRepository interfaceHistoryRepository;

    @Autowired
    FutureHoldConfigRepository futureHoldConfigRepository;

    @Autowired
    FutureHoldConfigHisRepository futureHoldConfigHisRepository;

    @Autowired
    GCScmToMesEngInformRepository gcScmToMesEngInformRepository;

    @Autowired
    GCScmToMesEngInformHisRepository gcScmToMesEngInformHisRepository;

    @Autowired
    WaferHoldRelationRepository waferHoldRelationRepository;

    @Autowired
    WaferHoldRelationHisRepository waferHoldRelationHisRepository;

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

    public void scmHold(List<String> materialLotUnitIdList, String actionCode, String actionReason, String actionRemarks) throws ClientException{
        try {
            for (String unitId : materialLotUnitIdList) {
                MaterialLotUnit materialLotUnit = materialLotUnitRepository.findByUnitIdAndStateIn(unitId, Lists.newArrayList(MaterialLotUnit.STATE_CREATE, MaterialLotUnit.STATE_IN, MaterialLotUnit.STATE_PACKAGE));
                if (materialLotUnit == null) {
                    // 不存在 则做预Hold
                    WaferHoldRelation waferHoldRelation = waferHoldRelationRepository.findByWaferIdAndType(unitId, WaferHoldRelation.HOLD_TYPE_SCM);
                    if(waferHoldRelation == null){
                        waferHoldRelation = new WaferHoldRelation();
                        waferHoldRelation.setWaferId(unitId);
                        waferHoldRelation.setHoldReason(actionReason);
                        waferHoldRelation.setType(WaferHoldRelation.HOLD_TYPE_SCM);
                        waferHoldRelationRepository.save(waferHoldRelation);

                        WaferHoldRelationHis history = (WaferHoldRelationHis) baseService.buildHistoryBean(waferHoldRelation, WaferHoldRelationHis.SCM_ADD);
                        waferHoldRelationHisRepository.save(history);
                    } else {
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_HOLD, unitId);
                    }
                } else {
                    MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotUnit.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
                    if(MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState())){
                        MaterialLotAction materialLotAction = new MaterialLotAction();
                        materialLotAction.setActionCode(actionCode);
                        materialLotAction.setActionReason(actionReason);
                        materialLotAction.setActionComment(actionRemarks);
                        mmsService.holdMaterialLot(materialLot, materialLotAction);
                    }
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void scmRelease(List<String> materialLotUnitIdList, String actionCode, String actionReason, String actionRemarks) throws ClientException{
        for (String unitId : materialLotUnitIdList) {
            MaterialLotUnit materialLotUnit = materialLotUnitRepository.findByUnitIdAndStateIn(unitId, Lists.newArrayList(MaterialLotUnit.STATE_CREATE, MaterialLotUnit.STATE_IN, MaterialLotUnit.STATE_PACKAGE));
            if (materialLotUnit == null) {
                WaferHoldRelation waferHoldRelation = waferHoldRelationRepository.findByWaferIdAndType(unitId, WaferHoldRelation.HOLD_TYPE_SCM);
                if(waferHoldRelation == null){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, unitId);
                } else {
                    waferHoldRelationRepository.delete(waferHoldRelation);

                    WaferHoldRelationHis history = (WaferHoldRelationHis) baseService.buildHistoryBean(waferHoldRelation, WaferHoldRelationHis.SCM_DELETE);
                    waferHoldRelationHisRepository.save(history);
                }
            } else {
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotUnit.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
                if(MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())){
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setActionCode(actionCode);
                    materialLotAction.setActionReason(actionReason);
                    materialLotAction.setActionComment(actionRemarks);
                    mmsService.releaseMaterialLot(materialLot, materialLotAction);
                }
            }
        }
    }

    public void scmAssign(String lotId, String vendor, String poId, String materialType, String remarks, String vendorAddress) throws ClientException{
        try {
            MaterialLot materialLot = getMaterialLotByNbTableNameAndLotId(SCM_TAG_TABLE_NAEM, lotId);

            scmAssignAndSaveHis(materialLot, materialType, vendor, poId, remarks, vendorAddress);

            //如果LOT已经装箱，验证箱中所有的LOT是否已经标注，如果全部标注，对箱号进行标注(箱中LOT的标注信息保持一致)
            if(!StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                MaterialLot parentMaterialLot = mmsService.getMLotByMLotId(materialLot.getParentMaterialLotId(), true);
                validateParentMLotScmAssign(parentMaterialLot, materialType, vendor, poId, remarks, vendorAddress);
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
    private void validateParentMLotScmAssign(MaterialLot parentMaterialLot, String materialType, String vendor, String poId, String remarks, String vendorAddress) throws ClientException{
        try {
            List<MaterialLot> materialLotList = packageService.getPackageDetailLots(parentMaterialLot.getObjectRrn());
            List<MaterialLot> unTagMaterialLotList = materialLotList.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getReserved54())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(unTagMaterialLotList)){
                scmAssignAndSaveHis(parentMaterialLot, materialType, vendor, poId, remarks, vendorAddress);
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
    private void scmAssignAndSaveHis(MaterialLot materialLot, String materialType, String vendor, String poId, String remarks, String vendorAddress) throws ClientException{
        try {
            materialLot.setReserved54(materialType);
            materialLot.setReserved55(vendor);
            materialLot.setReserved56(poId);
            materialLot.setReserved57(remarks);
            materialLot.setVenderAddress(vendorAddress);
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
     * scm 来料批次查询
     * @param lotIdList
     * @return
     * @throws ClientException
     */
    @Override
    public List<Map<String, String>> scmLotQuery(List<Map<String, String>> lotIdList) throws ClientException {
        try {
            NBTable nbTable = uiService.getNBTableByName(MaterialLot.GC_SCM_LOT_QUERY_WHERE_CLAUSE);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            List<Map<String, String>> materialLots = Lists.newArrayList();
            for (Map<String, String> lotIdMap : lotIdList) {
                String lotId = lotIdMap.get("lotId");
                if (!StringUtils.isNullOrEmpty(lotId)) {
                    StringBuffer clauseBuffer = new StringBuffer(whereClause);
                    clauseBuffer.append(" and lot_id = '" + lotId + "'");
                    List<MaterialLot> mLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), clauseBuffer.toString(), orderBy);
                    if (CollectionUtils.isNotEmpty(mLotList)) {
                        Map<String, String> mLotMap = Maps.newHashMap();
                        mLotMap.put("lotId", mLotList.get(0).getLotId());
                        mLotMap.put("boxId", mLotList.get(0).getParentMaterialLotId() == null ? StringUtils.EMPTY : mLotList.get(0).getParentMaterialLotId());
                        mLotMap.put("waferCount", mLotList.get(0).getCurrentSubQty().toString());
                        materialLots.add(mLotMap);
                    }
                }
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过物料批次生产订单号查询SCM晶圆信息
     * @param workOrderNo
     * @return
     * @throws ClientException
     */
    public List<Map<String, String>> queryScmWaferByWorkOrderNo(String workOrderNo) throws ClientException{
        try {
            Long timeStamp = System.currentTimeMillis();
            Map<String, Object> requestMap = Maps.newHashMap();
            requestMap.put("format", "json");
            requestMap.put("wo_codes", workOrderNo);
            requestMap.put("app_key", isProdEnv() ? PROD_APP_KEY : BATE_APP_KEY);
            requestMap.put("timestamp", timeStamp.toString());
            requestMap.put("system_id", SYSTEM_ID);
            requestMap.put("version", VERSION);

            List<String> paramStr = Lists.newArrayList();
            for (String key : requestMap.keySet()) {
                paramStr.add(key + "=" + requestMap.get(key));
            }
            String url = isProdEnv() ? scmUrl : wScmUrl + MSCM_QUERY_WAFER_BY_WONO_API;
            String destination = url + "?" + StringUtils.join(paramStr, "&");

            log.info("query waferInfo by wono requestString is " + destination);

            HttpEntity<byte[]> responseEntity = restTemplate.getForEntity(destination, byte[].class);
            String response = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response from bartender. Response is [%s]", response));
            }
            List<Map<String, String>> mapList = Lists.newArrayList();
            if (!StringUtils.isNullOrEmpty(response)) {
                QueryWaferResponse queryWaferResponse = DefaultParser.getObjectMapper().readerFor(QueryWaferResponse.class).readValue(response);
                if (!QueryEngResponse.SUCCESS_CODE.equals(queryWaferResponse.getCode())) {
                    throw new ClientException(queryWaferResponse.getMessage());
                }
                mapList = queryWaferResponse.getData();
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 添加、修改SCM工程品
     * @param lotEngInfoList
     * @throws ClientException
     */
    @Override
    public void scmSaveEngInfo(List<GCScmToMesEngInform> lotEngInfoList, String actionType) throws ClientException {
        try {
            for (GCScmToMesEngInform mesLot : lotEngInfoList) {
                if (StringUtils.isNullOrEmpty(mesLot.getLotId()) || StringUtils.isNullOrEmpty(mesLot.getProductId()) || StringUtils.isNullOrEmpty(mesLot.getWaferId())
                        || StringUtils.isNullOrEmpty(mesLot.getHoldFlag()) || StringUtils.isNullOrEmpty(mesLot.getHoldDesc())) {
                    throw new ClientException(GcExceptions.SCM_LOT_INFO_CONTAINS_EMPTY_VALUE);
                }
                GCScmToMesEngInform existMesLot = gcScmToMesEngInformRepository.findByLotId(mesLot.getLotId());
                if (EngManagerRequest.ACTION_TYPE_SAVE.equals(actionType)) {
                    if (existMesLot != null) {
                        throw new ClientParameterException(GcExceptions.SCM_LOT_ID_ALREADY_EXISTS, existMesLot.getLotId());
                    }
                    mesLot = gcScmToMesEngInformRepository.saveAndFlush(mesLot);
                    GCScmToMesEngInformHis scmToMesEngInformHis = (GCScmToMesEngInformHis) baseService.buildHistoryBean(mesLot, "CreateSCMEng");
                    gcScmToMesEngInformHisRepository.save(scmToMesEngInformHis);
                } else if (EngManagerRequest.ACTION_TYPE_UPDATE.equals(actionType)) {
                    if (existMesLot == null) {
                        throw new ClientParameterException(GcExceptions.SCM_LOT_ID_IS_NOT_EXIST, mesLot.getLotId());
                    }
                    PropertyUtils.copyProperties(mesLot, existMesLot);

                    existMesLot = gcScmToMesEngInformRepository.saveAndFlush(existMesLot);
                    GCScmToMesEngInformHis scmToMesEngInformHis = (GCScmToMesEngInformHis) baseService.buildHistoryBean(existMesLot, "UpdateMesEng");
                    gcScmToMesEngInformHisRepository.save(scmToMesEngInformHis);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除SCM工程品
     * @param lotEngInfoList
     * @throws ClientException
     */
    @Override
    public void scmDeleteEngInfo(List<GCScmToMesEngInform> lotEngInfoList) throws ClientException {
        try {
            for (GCScmToMesEngInform mesLot : lotEngInfoList) {
                GCScmToMesEngInform existMesLot = gcScmToMesEngInformRepository.findByLotId(mesLot.getLotId());
                if (existMesLot != null) {
                    gcScmToMesEngInformRepository.delete(existMesLot);
                    GCScmToMesEngInformHis gcScmToMesEngInformHis = (GCScmToMesEngInformHis) baseService.buildHistoryBean(existMesLot, "DeleteSCMEng");
                    gcScmToMesEngInformHisRepository.save(gcScmToMesEngInformHis);
                } else {
                    throw new ClientParameterException(GcExceptions.SCM_LOT_ID_IS_NOT_EXIST, mesLot.getLotId());
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 做接口的重试x
     */
    public void retry() throws ClientException{
        try {
            List<InterfaceFail> interfaceFails = interfaceFailRepository.findAll();
            if (CollectionUtils.isNotEmpty(interfaceFails)) {
                for (InterfaceFail interfaceFail : interfaceFails) {
                    String response = sendHttpRequest(interfaceFail.getDestination(), interfaceFail.getRequestTxt(), Maps.newHashMap(), InterfaceHistory.TRANS_TYPE_RETRY);
                    if (!StringUtils.isNullOrEmpty(response)) {
                        //对SCM验证Eng的物料批次做Update
                        validateAndUpdateMaterialLotEng(interfaceFail, response);
                        interfaceFailRepository.delete(interfaceFail);
                    }
                }
            }
        } catch(Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证retry请求是否是验证晶圆Eng
     * 修改并记录历史
     * @param interfaceFail
     * @param response
     * @throws ClientException
     */
    private void validateAndUpdateMaterialLotEng(InterfaceFail interfaceFail, String response) throws ClientException {
        try {
            if(SCM_RETRY_VALIDATE_MATERIAL_LOT_ENG.equals(interfaceFail.getDestination())){
                log.info("scm validate Eng retry start");
                QueryEngResponse queryEngResponse = DefaultParser.getObjectMapper().readerFor(QueryEngResponse.class).readValue(response);
                if (!QueryEngResponse.SUCCESS_CODE.equals(queryEngResponse.getCode())) {
                    throw new ClientException(queryEngResponse.getMessage());
                }
                List<Map> responseDataList = queryEngResponse.getData();
                if (CollectionUtils.isNotEmpty(responseDataList)) {
                    List<MaterialLotUnit> engMaterialLotUnitList = Lists.newArrayList();
                    for (Map responseData : responseDataList) {
                        String lotId = (String) responseData.get("lot_no");
                        String waferId = (String) responseData.get("wafer_id");
                        boolean engFlag = (boolean) responseData.get("is_eng");
                        if (engFlag) {
                            String unitId = lotId + StringUtils.SPLIT_CODE + waferId;
                            List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByUnitIdAndStateInAndReserved48IsNotNull(unitId, Lists.newArrayList(MaterialLotUnit.STATE_IN, MaterialLotUnit.STATE_CREATE));
                            //unitId只能存在一笔在制晶圆，故取第一笔
                            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                                MaterialLotUnit materialLotUnit = materialLotUnitList.get(0);
                                materialLotUnit.setProductType(MaterialLotUnit.PRODUCT_TYPE_ENG);
                                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                                engMaterialLotUnitList.add(materialLotUnit);

                                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, "SCMEng");
                                materialLotUnitHisRepository.save(materialLotUnitHistory);
                            }
                        }
                    }
                    log.info("scm validate Eng retry materialLotUnitList is " + engMaterialLotUnitList);
                    if(CollectionUtils.isNotEmpty(engMaterialLotUnitList)){
                        Map<String, List<MaterialLotUnit>> engMaterialLotMap = engMaterialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit :: getMaterialLotId));
                        for(String materialLotId : engMaterialLotMap.keySet()){
                            MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotId, ThreadLocalContext.getOrgRrn());
                            materialLot.setProductType(MaterialLotUnit.PRODUCT_TYPE_ENG);
                            materialLot = materialLotRepository.saveAndFlush(materialLot);

                            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, "SCMEng");
                            materialLotHistoryRepository.save(history);
                        }
                    }
                }
                log.info("scm validate Eng retry end ");
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
            materialLot.setVenderAddress(StringUtils.EMPTY);

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
                if (materialLotUnit.getCurrentQty().compareTo(BigDecimal.ZERO) <= 0){
                    throw new ClientParameterException(GcExceptions.THE_QUANTITY_FIELD_MUST_BE_GREATER_THAN_ZERO, materialLotUnit.getCurrentQty());
                }
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
            if (!StringUtils.isNullOrEmpty(response)) {
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
    public void sendMaterialStateReport(List<MaterialLotUnit> materialLotUnitList, String action) throws ClientException {
        try {
            MaterialLotStateReportRequest request = new MaterialLotStateReportRequest();
            RequestHeader requestHeader = new RequestHeader();
            requestHeader.setOrgName(ThreadLocalContext.getOrgName());
            requestHeader.setOrgRrn(ThreadLocalContext.getOrgRrn());
            requestHeader.setUsername(ThreadLocalContext.getUsername());
            requestHeader.setTransactionId(ThreadLocalContext.getTransRrn() == null? UUID.randomUUID().toString(): ThreadLocalContext.getTransRrn());
            request.setHeader(requestHeader);

            MaterialLotStateReportRequestBody requestBody = new MaterialLotStateReportRequestBody();
            List<Map<String, String>> reportDataList = Lists.newArrayList();
            if (log.isDebugEnabled()) {
                log.debug(String.format("scm report materialLot status materialLotUnitList is [%s]", materialLotUnitList));
            }
            for(MaterialLotUnit materialLotUnit: materialLotUnitList){
                Map<String, String> reportData = Maps.newHashMap();
                reportData.put("lotId", materialLotUnit.getLotId());
                String waferId = materialLotUnit.getUnitId().split("-")[1];
                reportData.put("waferId", waferId);
                reportDataList.add(reportData);
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format("report materialLot state reportDataList is [%s]", reportDataList));
            }
            requestBody.setActionType(action);
            requestBody.setMaterialLotList(reportDataList);
            request.setBody(requestBody);
            String responseStr = sendHttpRequest(scmUrl + MATERIAL_LOT_STATE_REPORT, request, Maps.newHashMap());

            if (!StringUtils.isNullOrEmpty(responseStr)) {
                Response response = DefaultParser.getObjectMapper().readerFor(Map.class).readValue(responseStr);
                if (!ResponseHeader.RESULT_SUCCESS.equals(response.getHeader().getResult())) {
                    throw new ClientException(response.getHeader().getResultCode());
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private String sendHttpRequest(String url, Object requestInfo, Map<String, String> httpHeaders) throws ClientException {
        return sendHttpRequest(url, requestInfo, httpHeaders, InterfaceHistory.TRANS_TYPE_NORMAL);
    }

    private String sendHttpRequest(String url, Object requestInfo, Map<String, String> httpHeaders, String transType) throws ClientException {
        String response = StringUtils.EMPTY;
        InterfaceHistory interfaceHistory = new InterfaceHistory();
        interfaceHistory.setTransType(transType);
        interfaceHistory.setDestination(url);
        try {
            String requestString = StringUtils.EMPTY;
            if (requestInfo instanceof String) {
                requestString = (String) requestInfo;
            } else {
                requestString = DefaultParser.getObjectMapper().writeValueAsString(requestInfo);
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format("Send data. RequestString is [%s]", requestString));
            }
            interfaceHistory.setRequestTxt(requestString);

            HttpHeaders headers = new HttpHeaders();
            String contentType = httpHeaders.get("contentType");
            if (StringUtils.isNullOrEmpty(contentType)) {
                contentType = "application/json";
            }
            headers.put("Content-Type", Lists.newArrayList(contentType));

//            String token = httpHeaders.get("authorization");
//            if (!StringUtils.isNullOrEmpty(token)) {
//                headers.put("authorization", Lists.newArrayList(token));
//            }

            String needTokenUrl = mScmUrl + MSCM_ADD_TRACKING_API;
            if (needTokenUrl.equals(url)){
                String token = getMScmToken();
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
            interfaceHistory.setResponseTxt(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String errorMessage = e.getMessage();
            if (!StringUtils.isNullOrEmpty(errorMessage) && errorMessage.length() > InterfaceHistory.ACTION_CODE_MAX_LENGTH) {
                errorMessage = errorMessage.substring(0, InterfaceHistory.ACTION_CODE_MAX_LENGTH);
            }
            interfaceHistory.setActionCode(errorMessage);
            interfaceHistory.setResult(InterfaceHistory.RESULT_FAIL);
        }
        interfaceHistory.setResponseTxt(response);
        if (InterfaceHistory.RESULT_FAIL.equals(interfaceHistory.getResult()) && !InterfaceHistory.TRANS_TYPE_RETRY.equals(transType)) {
            InterfaceFail interfaceFail = new InterfaceFail(interfaceHistory);
            interfaceFailRepository.save(interfaceFail);
        }
        interfaceHistoryRepository.save(interfaceHistory);
        return response;
    }

    private String getReceivingTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
        formatter.setLenient(false);
        return formatter.format(DateUtils.now());
    }

    public void addTracking(String orderId, String expressNumber, boolean isKuayueExprress) throws ClientException{
        try {
            List<Map> requestInfoList = Lists.newArrayList();
            Map requestInfo = Maps.newHashMap();
            requestInfo.put("send_code", orderId);
            if (isKuayueExprress) {
                requestInfo.put("logistics_receiving_time", getReceivingTime());
                requestInfo.put("logistics_company_name", "跨越物流");
                requestInfo.put("logistics_code", expressNumber);
            }
            requestInfoList.add(requestInfo);

            String response = sendHttpRequest(mScmUrl + MSCM_ADD_TRACKING_API, requestInfoList, Maps.newHashMap());
            if (!StringUtils.isNullOrEmpty(response)) {
                Map<String, Object> responseData = DefaultParser.getObjectMapper().readerFor(Map.class).readValue(response);
                Integer ret = (Integer) responseData.get("ret");
                if (200 != ret) {
                    throw new ClientParameterException(GcExceptions.MSCM_ERROR, responseData.get("msg"));
                }
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

            if (log.isDebugEnabled()) {
                log.debug(String.format("Send addScmTracking. RequestString is [%s]", requestInfoList));
            }

            String response = sendHttpRequest(mScmUrl + MSCM_ADD_TRACKING_API, requestInfoList, Maps.newHashMap());
            if (!StringUtils.isNullOrEmpty(response)) {
                Map<String, Object> responseData = DefaultParser.getObjectMapper().readerFor(Map.class).readValue(response);
                Integer ret = (Integer) responseData.get("ret");
                if (200 != ret) {
                    throw new ClientParameterException(GcExceptions.MSCM_ERROR, responseData.get("msg"));
                }
            }

        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String getMScmToken() throws ClientException{
        try {
            String token = StringUtils.EMPTY;
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.put("contentType", Lists.newArrayList("application/x-www-form-urlencoded"));

            Map<String, String> requestMap = Maps.newHashMap();
            requestMap.put("app_name", mScmUsername);
            requestMap.put("app_secret", mScmPassword);
            requestMap.put("service", MSCM_SERVICE_NAME);

            MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
            for (String key : requestMap.keySet()) {
                postParameters.add(key, requestMap.get(key));
            }

            if (log.isDebugEnabled()) {
                String requestString = DefaultParser.getObjectMapper().writeValueAsString(requestMap);
                log.debug(String.format("Send data. RequestString is [%s]", requestString));
            }

            HttpEntity<MultiValueMap> httpEntity = new HttpEntity<>(postParameters, httpHeader);
            Map<String, Object> responseMap = restTemplate.postForObject(new URI(mScmUrl + MSCM_TOKEN_API), httpEntity, Map.class);

            if (log.isDebugEnabled()) {
                String response = DefaultParser.writerJson(responseMap);
                log.debug(String.format("Get response by scm. Response is [%s]", response));
            }

            if (!responseMap.isEmpty()) {
                Integer ret = (Integer) responseMap.get("ret");
                if (200 != ret) {
                    throw new ClientParameterException(GcExceptions.MSCM_ERROR, responseMap.get("msg"));
                }
                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                token = (String) data.get("token");
            }
            return token;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


}
