package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.vanchip.dto.vlm.GetBoxID;
import com.newbiest.vanchip.dto.vlm.VLMRequest;
import com.newbiest.vanchip.dto.vlm.VLMRequestBody;
import com.newbiest.vanchip.service.VLMService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import static org.apache.http.impl.client.HttpClientBuilder.create;

@Transactional
@Component
@Slf4j
@Service
public class VLMServiceImpl implements VLMService {

    /**
     * 连接VLM系统的超时时间 单位秒
     */
    public static final int VML_CONNECTION_TIME_OUT = 30;

    /**
     * 读取VLM系统的超时时间 单位秒
     */
    public static final int VML_READ_TIME_OUT = 60;

    public static final String API_URL_BIND_REEL_TO_BOX = "BindReelToBox";
    public static final String API_URL_UNBIND_REEL_TO_BOX ="UnbindBox";
    public static final String API_URL_GET_BOX_ID ="getBoxID_20190403";

    @Value("${vc.vlm.vlmUrl}")
    private String vlmUrl;

    @Value("${vc.vlm.pOper}")
    private String pOper;

    @Value("${vc.vlm.pOperPWD}")
    private String pOperPWD;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        CloseableHttpClient client = createHttpClient().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        requestFactory.setConnectTimeout(VML_CONNECTION_TIME_OUT * 1000);
        requestFactory.setReadTimeout(VML_READ_TIME_OUT * 1000);
        restTemplate = new RestTemplate(requestFactory);
    }

    protected HttpClientBuilder createHttpClient() {
        return create().useSystemProperties().disableRedirectHandling().disableCookieManagement();
    }


    public void bindReelToBox(MaterialLot boxMaterialLot, List<MaterialLot> materialLots) throws ClientException {
        try {

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void unbindReelToBox(MaterialLot boxMaterialLot, List<MaterialLot> materialLots) throws ClientException {
        try {

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取外箱号
     * @throws ClientException
     */
    public void getBoxId(MaterialLot boxMaterialLot) throws ClientException {
        try {
            VLMRequest vlmRequest = new VLMRequest();
            VLMRequestBody vlmRequestBody = new VLMRequestBody();

            GetBoxID getBoxIDRequest = new GetBoxID();
            getBoxIDRequest.setManufacturer("xx");
            getBoxIDRequest.setCompname("xxx");
            getBoxIDRequest.setAmount("20");
            getBoxIDRequest.setPOper(pOper);
            getBoxIDRequest.setPOperPWD(pOperPWD);
            vlmRequestBody.setGetBoxID(getBoxIDRequest);
            vlmRequest.setBody(vlmRequestBody);

            String requestString = objectToXml(vlmRequest);
            String responeString = sendVMLRequest(requestString);

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String sendVMLRequest(String requestString) throws ClientException {
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Send data vlm. RequestString is [%s]", requestString));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.CONTENT_TYPE, Lists.newArrayList("application/json"));
            RequestEntity<byte[]> requestEntity = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(vlmUrl));

            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
            String responseString = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response by vlm. ResponseString is [%s]", responseString));
            }
            return responseString;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String objectToXml(Object object)throws ClientException {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            String result = writer.toString();
            return result;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
}
