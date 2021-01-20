package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.vanchip.dto.issue.*;
import com.newbiest.vanchip.dto.returnlot.*;
import com.newbiest.vanchip.service.MesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;

/**
 * 连接MES的服务相关
 */
@Transactional
@Component
@Slf4j
@Service
public class MesServiceImpl implements MesService {

    /**
     * 连接MES的超时时间 单位秒
     */
    public static final int MES_CONNECTION_TIME_OUT = 30;

    /**
     * 读取MES的超时时间 单位秒
     */
    public static final int MES_READ_TIME_OUT = 60;
    
    //private String mesUrl ="http://192.168.28.61:7001/mycim2";
    private String mesUrl ="http://192.168.28.161:7001/mycim2";

    public static final String ISSUE_URL = "/wms/wmsIssue.spring";
    public static final String RETURN_MLOT_URL = "/wms/wmsReturn.spring";

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setConnectTimeout(MES_CONNECTION_TIME_OUT * 1000);
//        requestFactory.setReadTimeout(MES_READ_TIME_OUT * 1000);
        restTemplate = new RestTemplate();
    }

    public void issueMLotByDocRequestMes(List<MaterialLot> materialLots) throws ClientException {
        try {
            IssueMLotRequest request = new IssueMLotRequest();
            IssueMLotRequestBody requestBody = new IssueMLotRequestBody();
            IssueMLotRequestHeader requestHeader = IssueMLotRequestHeader.buildDefaultRequestHeader(IssueMLotRequestHeader.ISSUE_MLOT_MESSAGE_NAME);

            requestBody.setActionType(IssueMLotRequestBody.ACTION_TYPE_ISSUE_MLOT);
            requestBody.setWmsMaterialLots(materialLots);
            request.setBody(requestBody);
            request.setHeader(requestHeader);

            String requestString = DefaultParser.getObjectMapper().writeValueAsString(request);
            HttpHeaders headers = new HttpHeaders();
            headers.put("Content-Type", Lists.newArrayList("application/json"));

            RequestEntity<byte[]> requestEntity = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(mesUrl + ISSUE_URL));
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);

            String responseString = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response by mes. Response is [%s]", responseString));
            }

            IssueMLotResponse response = DefaultParser.getObjectMapper().readValue(responseString, IssueMLotResponse.class);
            IssueMLotResponseHeader responseHeader = response.getHeader();
            if (IssueMLotResponseHeader.RESULT_FAIL.equals(responseHeader.getResult())){
                throw new ClientException(responseHeader.getResultCode());
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void issueMLotByDocLineRequestMes(List<MaterialLot> materialLots) throws ClientException{
        try {
            IssueMLotRequest issueMLotRequest = new IssueMLotRequest();
            IssueMLotRequestBody issueMLotRequestBody = new IssueMLotRequestBody();
            IssueMLotRequestHeader header = IssueMLotRequestHeader.buildDefaultRequestHeader(IssueMLotRequestHeader.ISSUE_MATERIAL_MESSAGE_NAME);

            issueMLotRequestBody.setActionType(IssueMLotRequestBody.ACTION_TYPE_ISSUE_MATERIAL);
            issueMLotRequestBody.setWmsMaterialLots(materialLots);
            issueMLotRequest.setBody(issueMLotRequestBody);
            issueMLotRequest.setHeader(header);

            String requestString = DefaultParser.getObjectMapper().writeValueAsString(issueMLotRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.put("Content-Type", Lists.newArrayList("application/json"));

            RequestEntity<byte[]> request = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(mesUrl + ISSUE_URL));
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(request, byte[].class);

            String response = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response by mes. Response is [%s]", response));
            }

            IssueMLotResponse issueMLotResponse = DefaultParser.getObjectMapper().readValue(response, IssueMLotResponse.class);
            IssueMLotResponseHeader responseHeader = issueMLotResponse.getHeader();
            if (IssueMLotResponseHeader.RESULT_FAIL.equals(responseHeader.getResult())){
                throw new ClientException(responseHeader.getResultCode());
            }

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
