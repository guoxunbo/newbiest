package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.gc.model.MesWaferReceive;
import com.newbiest.gc.service.MesService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.apache.http.impl.client.HttpClientBuilder.create;

/**
 * 请求Mes
 * @author guozhangLuo
 * @date 2020-09-23
 */
@Service
@Slf4j
@Data
@EnableAsync
public class MesServiceImpl implements MesService {

    /**
     * 连接Mes的超时时间 单位秒
     */
    public static final int MES_CONNECTION_TIME_OUT = 300;

    public static final String MES_FACILITY_ID = "GC";

    public static final String MESSAGE_INFO = "success";

    /**
     * 读取MES的超时时间 单位秒
     */
    public static final int MES_READ_TIME_OUT = 120;

    public static final String PLAN_LOT_API = "/wms/planLot.spring";

    public static final String SAVE_BACKEND_WAFER_RECEIVE_API = "/wms/saveWaferReceive.spring";

    private RestTemplate restTemplate;

    @Value("${gc.mesUrl}")
    private String mesUrl;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @PostConstruct
    public void init() {
        CloseableHttpClient client = createHttpClient().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        requestFactory.setConnectTimeout(MES_CONNECTION_TIME_OUT * 1000);
        requestFactory.setReadTimeout(MES_READ_TIME_OUT * 1000);
        restTemplate = new RestTemplate(requestFactory);
    }

    protected HttpClientBuilder createHttpClient() {
        return create().useSystemProperties().disableRedirectHandling().disableCookieManagement();
    }

    /**
     * 发料时将发料的Unit 计划投批
     * @param materialLots
     * @throws ClientException
     */
    @Async
    public void materialLotUnitPlanLot(List<MaterialLot> materialLots) throws ClientException {
        try {
            List<String> unitIdList = Lists.newArrayList();
            List<String> rwWaferSourceList = Lists.newArrayList(MaterialLot.SCP_WAFER_SOURCE, MaterialLot.CP_CHANGGE_RW_WAFER_SOURCE, MaterialLot.LCP_WAFER_SOURCE, MaterialLot.RW_WAFER_SOURCE);
            for(MaterialLot materialLot : materialLots){
                if(MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) ||
                        MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CATEGORY_RW.equals(materialLot.getReserved7()) ||
                        MaterialLotUnit.PRODUCT_CLASSIFY_SOC.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CATEGORY_SOC.equals(materialLot.getReserved7())){
                    if(!StringUtils.isNullOrEmpty(materialLot.getInnerLotId()) && rwWaferSourceList.contains(materialLot.getReserved50())){
                        unitIdList.add(materialLot.getInnerLotId());
                    } else {
                        unitIdList.add(materialLot.getLotId());
                    }
                } else {
                    if(MaterialLot.IMPORT_COB.equals(materialLot.getReserved49())){
                        String workOrderId = materialLot.getWorkOrderId();
                         if(!StringUtils.isNullOrEmpty(workOrderId) && workOrderId.startsWith("T")){
                             List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                             for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                                 unitIdList.add(materialLotUnit.getUnitId());
                             }
                         }
                    } else {
                        List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                        for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                            unitIdList.add(materialLotUnit.getUnitId());
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(unitIdList)){
                Map<String, Object> requestInfo = Maps.newHashMap();
                requestInfo.put("planLotUnit", unitIdList);
                requestInfo.put("userName", ThreadLocalContext.getUsername());
                requestInfo.put("messageName", "materialLotUnitManager");
                requestInfo.put("facilityId", MES_FACILITY_ID);

                String requestString = DefaultParser.getObjectMapper().writeValueAsString(requestInfo);
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Sending to mes. RequestString is [%s]", requestString));
                }

                HttpHeaders headers = new HttpHeaders();
                headers.put("Content-Type", Lists.newArrayList("application/json"));

                RequestEntity<byte[]> request = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(mesUrl + PLAN_LOT_API));
                ResponseEntity<byte[]> responseEntity = restTemplate.exchange(request, byte[].class);
                String response = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Get response by mes. Response is [%s]", response));
                }

                if(!MESSAGE_INFO.equals(response)){
                    throw new ClientParameterException(response);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 将晶圆信息记录到Mes的BackendWaferReceive及历史表中
     * @param materialLots
     * @throws ClientException
     */
    public void saveBackendWaferReceive(List<MaterialLot> materialLots) throws ClientException {
        try {
            List<String> mLotIdList = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(materialLots)){
                for(MaterialLot materialLot: materialLots){
                    mLotIdList.add(materialLot.getMaterialLotId());
                }
                Map<String, Object> requestInfo = Maps.newHashMap();
                requestInfo.put("mLotIdList", mLotIdList);
                requestInfo.put("userName", ThreadLocalContext.getUsername());
                requestInfo.put("messageName", "materialLotUnitManager");
                requestInfo.put("facilityId", MES_FACILITY_ID);

                String requestString = DefaultParser.getObjectMapper().writeValueAsString(requestInfo);
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Sending to mes. RequestString is [%s]", requestString));
                }

                HttpHeaders headers = new HttpHeaders();
                headers.put("Content-Type", Lists.newArrayList("application/json"));

                RequestEntity<byte[]> request = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(mesUrl + SAVE_BACKEND_WAFER_RECEIVE_API));
                ResponseEntity<byte[]> responseEntity = restTemplate.exchange(request, byte[].class);
                String response = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Get response by mes. Response is [%s]", response));
                }

                if(!MESSAGE_INFO.equals(response)){
                    throw new ClientParameterException(response);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}
