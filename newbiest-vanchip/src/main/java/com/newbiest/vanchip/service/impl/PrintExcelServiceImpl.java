package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.vanchip.dto.mes.MesRequest;
import com.newbiest.vanchip.dto.mes.MesResponse;
import com.newbiest.vanchip.dto.mes.MesResponseHeader;
import com.newbiest.vanchip.dto.print.PrintExcelRequest;
import com.newbiest.vanchip.dto.print.PrintExcelRequestBody;
import com.newbiest.vanchip.dto.print.model.*;
import com.newbiest.vanchip.service.PrintExcelService;
import lombok.extern.slf4j.Slf4j;
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


@Transactional
@Component
@Slf4j
@Service
public class PrintExcelServiceImpl implements PrintExcelService {

    public static final int MES_CONNECTION_TIME_OUT = 30;

    public static final int MES_READ_TIME_OUT = 60;

    public static final String PRINT_URL = "/printExcel";

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(MES_CONNECTION_TIME_OUT * 1000);
        requestFactory.setReadTimeout(MES_READ_TIME_OUT * 1000);
        restTemplate = new RestTemplate();
    }

    public void printExcel(ExcelPrintInfo excelPrintInfo) throws ClientException{
        try {
            PrintExcelRequest request = new PrintExcelRequest();
            PrintExcelRequestBody requestBody = new PrintExcelRequestBody();

            requestBody.setExcelPrintInfo(excelPrintInfo);
            request.setBody(requestBody);
            sendPrintRequest(request, PRINT_URL, null);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void printExcel(CocPrintInfo cocPrintInfo, PackingListPrintInfo packingListPrintInfo, ShippingListPrintInfo shippingListPrintInfo, PKListPrintInfo pKListPrintInfo, String actionType) throws ClientException{
        try {
            PrintExcelRequest request = new PrintExcelRequest();
            PrintExcelRequestBody requestBody = new PrintExcelRequestBody();

            requestBody.setActionType(actionType);

            requestBody.setCocPrintInfo(cocPrintInfo);
            requestBody.setPackingListPrintInfo(packingListPrintInfo);
            requestBody.setShippingListPrintInfo(shippingListPrintInfo);
            requestBody.setPKListPrintInfo(pKListPrintInfo);

            request.setBody(requestBody);
            sendPrintRequest(request, PRINT_URL, null);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void sendPrintRequest(MesRequest mesRequest, String apiUrl, Class responseClass) throws ClientException{
        try {
            String transactionIp = ThreadLocalContext.getTransactionIp();
            String requestUrl = "http://" + transactionIp + ":9095/wmsPrint";

            String requestString = DefaultParser.getObjectMapper().writeValueAsString(mesRequest);
            if (log.isDebugEnabled()) {
                log.debug(String.format("Send data. RequestString is [%s]", requestString));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.CONTENT_TYPE, Lists.newArrayList("application/json"));
            RequestEntity<byte[]> requestEntity = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI( requestUrl + apiUrl));

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
