package com.newbiest.gc.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.service.ScmService;
import com.newbiest.gc.service.model.QueryEngResponse;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.msg.DefaultParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.http.impl.client.HttpClientBuilder.create;

/**
 * 请求GC的SCM系统
 * API地址：https://www.showdoc.cc/949149104775520?page_id=4964217376034753 密码123456
 * @author guoxunbo
 * @date 2020-08-09 10:40
 */
@Service
@Slf4j
@Data
public class ScmServiceImpl implements ScmService {

    /**
     * 连接SCM的超时时间
     */
    public static final int SCM_CONNECTION_TIME_OUT = 30;

    /**
     * 读取SCM的超时时间
     */
    public static final int SCM_READ_TIME_OUT = 60;

    public static final String REFERENCE_NAME_FOR_SCM = "SCMImportType";

    public static final String QUERY_ENG_API = "/api/wip/sync-eng/query";

    private RestTemplate restTemplate;

    @Value("${gc.scmUrl}")
    private String scmUrl;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    UIService uiService;

    @PostConstruct
    public void init() {
        CloseableHttpClient client = createHttpClient().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        requestFactory.setConnectTimeout(SCM_CONNECTION_TIME_OUT);
        requestFactory.setReadTimeout(SCM_READ_TIME_OUT);
        restTemplate = new RestTemplate(requestFactory);
    }

    protected HttpClientBuilder createHttpClient() {
        return create().useSystemProperties().disableRedirectHandling().disableCookieManagement();
    }

    /**
     * 根据标记调用SCM系统进行判断是否是ENG产品
     * @param materialLotUnits
     * @throws ClientException
     */
    public void assignEngFlag(List<MaterialLotUnit> materialLotUnits) throws ClientException {
        try {
            List<NBOwnerReferenceList> connectScmImportTypeList = getImportTypeForScm();
            if (CollectionUtils.isEmpty(connectScmImportTypeList)) {
                log.warn("OwnerRefList SCMImportType is not config. so does not connect to scm");
                return;
            }
            List<Map> requestWaferList = Lists.newArrayList();
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                Map<String, String> requestInfo = Maps.newHashMap();
                if (StringUtils.isNullOrEmpty(materialLotUnit.getLotId())) {
                    log.warn(String.format("MaterialUnit [%s] has no lotId.", materialLotUnit.getUnitId()));
                    continue;
                }
                // 验证导入类型是否在要发给SCM的类型中
                Optional optional = connectScmImportTypeList.stream().filter(connectScmImportType -> connectScmImportType.getKey().equals(materialLotUnit.getReserved49())).findFirst();
                if (!optional.isPresent()) {
                    log.warn(String.format("MaterialLotUnit [%s]'s import type is not in SCMImportType so skip it", materialLotUnit.getUnitId()));
                    continue;
                }

                requestInfo.put("lot_no", materialLotUnit.getLotId());
                String waferId = materialLotUnit.getUnitId().substring(materialLotUnit.getUnitId().indexOf(StringUtils.SPLIT_CODE) + 1);
                requestInfo.put("wafer_id", waferId);
                requestWaferList.add(requestInfo);
            }
            Map<String, Object> requestInfo = Maps.newHashMap();
            requestInfo.put("engs", requestWaferList);

            String requestString = DefaultParser.getObjectMapper().writeValueAsString(requestInfo);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Sending to scm. RequestString is [%s]", requestString));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.put("Content-Type", Lists.newArrayList("application/json"));

            RequestEntity<byte[]> request = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(scmUrl + QUERY_ENG_API));
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(request, byte[].class);
            String response = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response by scm. Response is [%s]", response));
            }
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
                        engWaferIdList.add(lotId + StringUtils.SPLIT_CODE + waferId);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(engWaferIdList)) {
                log.debug(String.format("Eng Wafer List is [%s]", engWaferIdList));
                materialLotUnitRepository.updateProdTypeByUnitIds(MaterialLotUnit.PRODUCT_TYPE_ENG, engWaferIdList);
            }
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
}
