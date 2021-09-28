package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.vanchip.dto.mes.MesRequest;
import com.newbiest.vanchip.dto.mes.MesResponse;
import com.newbiest.vanchip.dto.mes.MesResponseHeader;
import com.newbiest.vanchip.dto.mes.issue.IssueMLotRequest;
import com.newbiest.vanchip.dto.mes.issue.IssueMLotRequestBody;
import com.newbiest.vanchip.dto.mes.issue.IssueMLotRequestHeader;
import com.newbiest.vanchip.dto.mes.receive.ReceiveMLotRequest;
import com.newbiest.vanchip.dto.mes.receive.ReceiveMLotRequestBody;
import com.newbiest.vanchip.dto.mes.receive.ReceiveMLotRequestHeader;
import com.newbiest.vanchip.dto.mes.returnlot.ReturnMLotRequest;
import com.newbiest.vanchip.dto.mes.returnlot.ReturnMLotRequestBody;
import com.newbiest.vanchip.dto.mes.returnlot.ReturnMLotRequestHeader;
import com.newbiest.vanchip.service.MesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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

    @Value("${vc.mesUrl}")
    private String mesUrl;

    public static final String ISSUE_URL = "/wms/issueMLot.spring";
    public static final String RETURN_URL = "/wms/returnMLot.spring";
    public static final String RECEIVE_URL = "/wms/shipVBox.spring";
    public static final String RECEIVE_INFERIOR_PRODUCT_URL = "/wms/storageOfDefectiveProducts.spring";
    public static final String ISSUE_PARTS_URL = "/wms/partsApplyAlreadyQty.spring";

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(MES_CONNECTION_TIME_OUT * 1000);
        requestFactory.setReadTimeout(MES_READ_TIME_OUT * 1000);
        restTemplate = new RestTemplate();
    }

    public void issuePartsMLot(DocumentLine documentLine) throws ClientException{
        try {
            IssueMLotRequestHeader requestHeader = new IssueMLotRequestHeader();

            IssueMLotRequest request = new IssueMLotRequest();
            IssueMLotRequestBody requestBody = new IssueMLotRequestBody();

            requestBody.setDocumentLine(documentLine);
            request.setBody(requestBody);
            request.setHeader(requestHeader);

            sendMesRequest(request, ISSUE_PARTS_URL, null);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void receiveInferiorProduct(List<String> materialLotIdList) throws ClientException{
        try {
            ReceiveMLotRequestHeader requestHeader = new ReceiveMLotRequestHeader();

            ReceiveMLotRequest request = new ReceiveMLotRequest();
            ReceiveMLotRequestBody requestBody = new ReceiveMLotRequestBody();

            requestBody.setMaterialLotIds(materialLotIdList);
            request.setBody(requestBody);
            request.setHeader(requestHeader);

            sendMesRequest(request, RECEIVE_INFERIOR_PRODUCT_URL, null);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void receiveFinishGood(List<String> materialLotIdList) throws ClientException{
        try {
            ReceiveMLotRequestHeader requestHeader = new ReceiveMLotRequestHeader();

            ReceiveMLotRequest request = new ReceiveMLotRequest();
            ReceiveMLotRequestBody requestBody = new ReceiveMLotRequestBody();

            requestBody.setMaterialLotIds(materialLotIdList);
            request.setBody(requestBody);
            request.setHeader(requestHeader);

            sendMesRequest(request, RECEIVE_URL, null);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void returnMLot(List<String> materialLotIdList) throws ClientException{
        try {
            ReturnMLotRequestHeader requestHeader = new ReturnMLotRequestHeader();

            ReturnMLotRequest request = new ReturnMLotRequest();
            ReturnMLotRequestBody requestBody = new ReturnMLotRequestBody();

            requestBody.setMaterialLotIds(materialLotIdList);
            request.setBody(requestBody);
            request.setHeader(requestHeader);

            sendMesRequest(request, RETURN_URL, null);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void issueMLot(List<String> materialLotIdList) throws ClientException{
        try {
            IssueMLotRequestHeader requestHeader = new IssueMLotRequestHeader();

            IssueMLotRequest request = new IssueMLotRequest();
            IssueMLotRequestBody requestBody = new IssueMLotRequestBody();

            requestBody.setMaterialLotIds(materialLotIdList);
            request.setBody(requestBody);
            request.setHeader(requestHeader);

            sendMesRequest(request, ISSUE_URL, null);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void sendMesRequest(MesRequest mesRequest, String apiUrl, Class responseClass) throws ClientException{
        try {
            String requestString = DefaultParser.getObjectMapper().writeValueAsString(mesRequest);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Send data mes. RequestString is [%s]", requestString));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.CONTENT_TYPE, Lists.newArrayList("application/json"));
            RequestEntity<byte[]> requestEntity = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(mesUrl + apiUrl));

            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
            String responseString = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response by mes. Response is [%s]", responseString));
            }
            if (responseClass == null) {
                responseClass = MesResponse.class;
            }
            MesResponse response = (MesResponse) DefaultParser.getObjectMapper().readValue(responseString, responseClass);
            MesResponseHeader responseHeader = response.getHeader();
            if (MesResponseHeader.RESULT_FAIL.equals(responseHeader.getResult())){
                throw new ClientException(responseHeader.getResultCode());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}
