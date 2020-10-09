package com.newbiest.gc.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.service.MesService;
import com.newbiest.gc.service.model.QueryEngResponse;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.msg.DefaultParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.init.Jackson2ResourceReader;
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

import static com.newbiest.mms.exception.MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST;
import static org.apache.http.impl.client.HttpClientBuilder.create;

/**
 * 请求Mes
 * @author guozhangLuo
 * @date 2020-09-23
 */
@Service
@Slf4j
@Data
public class MesServiceImpl implements MesService {

    /**
     * 连接Mes的超时时间 单位秒
     */
    public static final int MES_CONNECTION_TIME_OUT = 30;

    public static final String MES_FACILITY_ID = "GC";

    public static final String MESSAGE_INFO = "success";

    /**
     * 读取MES的超时时间 单位秒
     */
    public static final int MES_READ_TIME_OUT = 60;

    public static final String PLAN_LOT_API = "/wms/planLot.spring";

    private RestTemplate restTemplate;

    @Value("${gc.mesUrl}")
    private String mesUrl;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @Autowired
    UIService uiService;

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
    public void materialLotUnitPlanLot(List<MaterialLot> materialLots) throws ClientException {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            List<String> unitIdList = Lists.newArrayList();
            for(MaterialLot materialLot : materialLots){
                if(MaterialLot.IMPORT_SENSOR_CP.equals(materialLot.getReserved49()) || MaterialLot.IMPORT_LCD_CP.equals(materialLot.getReserved49()) ){
                    unitIdList.add(materialLot.getLotId());
                } else {
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                    for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                        unitIdList.add(materialLotUnit.getUnitId());
                    }
                }
            }

            Map<String, Object> requestInfo = Maps.newHashMap();
            requestInfo.put("planLotUnit", unitIdList);
            requestInfo.put("userName", sc.getUsername());
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
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}
