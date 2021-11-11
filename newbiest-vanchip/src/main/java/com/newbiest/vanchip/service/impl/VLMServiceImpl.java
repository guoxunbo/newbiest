package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.InterfaceHistory;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.repository.InterfaceHistoryRepository;
import com.newbiest.vanchip.dto.vlm.VLMResult;
import com.newbiest.vanchip.dto.vlm.bind.BindReelToBox;
import com.newbiest.vanchip.dto.vlm.bind.BindReelToBoxRequest;
import com.newbiest.vanchip.dto.vlm.bind.BindReelToBoxRequestBody;
import com.newbiest.vanchip.dto.vlm.box.GetBoxID;
import com.newbiest.vanchip.dto.vlm.box.GetBoxIDRequest;
import com.newbiest.vanchip.dto.vlm.box.GetBoxIDRequestBody;
import com.newbiest.vanchip.dto.vlm.unbind.UnBindReelToBox;
import com.newbiest.vanchip.dto.vlm.unbind.UnBindReelToBoxRequestBody;
import com.newbiest.vanchip.dto.vlm.unbind.UnbindReelToBoxRequest;
import com.newbiest.vanchip.service.VLMService;
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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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

    public static final String BIND_REEL_TO_BOX_RESULT_ELEMENT_NAME = "BindReelToBoxResult";
    public static final String UNBIND_REEL_TO_BOX_RESULT_ELEMENT_NAME ="UnbindBoxResult";
    public static final String GET_BOX_ID_RESULT_ELEMENT_NAME ="getBoxID_20190403Result";

    @Value("${vc.vlm.vlmUrl}")
    private String vlmUrl;

    @Value("${vc.vlm.pOper}")
    private String pOper;

    @Value("${vc.vlm.pOperPWD}")
    private String pOperPWD;

    private RestTemplate restTemplate;

    @Autowired
    InterfaceHistoryRepository interfaceHistoryRepository;

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


    /**
     * 客户的箱号和客户外箱号绑定
     * @param boxMaterialLot
     * @param materialLots
     * @throws ClientException
     */
    public void bindReelToBox(MaterialLot boxMaterialLot, List<MaterialLot> materialLots) throws ClientException {
        try {
            BindReelToBoxRequest request = new BindReelToBoxRequest();
            BindReelToBoxRequestBody requestBody = new BindReelToBoxRequestBody();
            BindReelToBox bindReelToBox = new BindReelToBox();

            List<String> customerMrnList = materialLots.stream().map(mLot -> mLot.getReserved63()).collect(Collectors.toList());
            String customerMrn = StringUtils.join(customerMrnList, ",");

            bindReelToBox.setBoxid(boxMaterialLot.getReserved60());
            bindReelToBox.setReels(customerMrn);
            bindReelToBox.setPOper(pOper);
            bindReelToBox.setPOperPWD(pOperPWD);

            requestBody.setBindReelToBox(bindReelToBox);
            request.setBody(requestBody);

            String requestString = objectToXml(request);

            sendVMLRequest(requestString, BIND_REEL_TO_BOX_RESULT_ELEMENT_NAME);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 取消外箱箱号绑定。
     * @param customerBoxId
     * @throws ClientException
     */
    public void unbindReelToBox(String customerBoxId) throws ClientException {
        try {
            UnbindReelToBoxRequest request = new UnbindReelToBoxRequest();
            UnBindReelToBoxRequestBody requestBody = new UnBindReelToBoxRequestBody();
            UnBindReelToBox unbindReelToBox = new UnBindReelToBox();

            unbindReelToBox.setBoxid(customerBoxId);

            unbindReelToBox.setPOper(pOper);
            unbindReelToBox.setPOperPWD(pOperPWD);

            request.setBody(requestBody);
            requestBody.setUnBindReelToBox(unbindReelToBox);

            String requestString = objectToXml(request);
            sendVMLRequest(requestString, UNBIND_REEL_TO_BOX_RESULT_ELEMENT_NAME);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取客户外箱号
     * @throws ClientException
     */
    public String getBoxId(MaterialLot boxMaterialLot) throws ClientException {
        try {
            GetBoxIDRequest request = new GetBoxIDRequest();
            GetBoxIDRequestBody vlmRequestBody = new GetBoxIDRequestBody();
            GetBoxID getBoxID = new GetBoxID();

            getBoxID.setManufacturer(boxMaterialLot.getReserved57());
            getBoxID.setCompname(boxMaterialLot.getReserved64());
            getBoxID.setAmount(boxMaterialLot.getCurrentQty().toPlainString());
            getBoxID.setPOper(pOper);
            getBoxID.setPOperPWD(pOperPWD);

            vlmRequestBody.setGetBoxID(getBoxID);
            request.setBody(vlmRequestBody);

            String requestString = objectToXml(request);
            VLMResult vlmResult = sendVMLRequest(requestString, GET_BOX_ID_RESULT_ELEMENT_NAME);
            String boxId = vlmResult.getReturn_result();
            return boxId;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public VLMResult sendVMLRequest(String requestString, String resultElementName) throws ClientException {

        InterfaceHistory interfaceHistory = new InterfaceHistory();
        interfaceHistory.setSystemName(InterfaceHistory.SYSTEM_NAME_VIVO_VLM);
        interfaceHistory.setTransType(InterfaceHistory.TRANS_TYPE_NORMAL);
        interfaceHistory.setDestination(vlmUrl);
        interfaceHistory.setRequestTxt(requestString);

        String responseString = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Send data vlm. RequestString is [%s]", requestString));
            }
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.CONTENT_TYPE, Lists.newArrayList("text/xml"));
            RequestEntity<byte[]> requestEntity = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(vlmUrl));

            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
            responseString = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response by vlm. ResponseString is [%s]", responseString));
            }
            interfaceHistory.setResponseTxt(responseString);
            interfaceHistory.setResult(InterfaceHistory.RESULT_SUCCESS);
            interfaceHistoryRepository.saveAndFlush(interfaceHistory);

            VLMResult result = result(responseString, resultElementName);
            if (VLMResult.STATUS_FAIL.equals(result.getReturn_status())){
                throw new ClientException(result.getReturn_message());
            }
            return result;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String objectToXml(Object object) throws ClientException {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StringUtils.CHARSET_UTF_8);

            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            String result = writer.toString();
            return result;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *
     * @param responseString SOAP XML
     * @param resultElementName 返回结果的元素名称
     * @return
     * @throws ClientException
     */
    public VLMResult result(String responseString, String resultElementName) throws ClientException {
        try {
            SOAPMessage soapMessage = formatSoapString(responseString);
            String resultJsonStr = getElementValueByName(soapMessage.getSOAPBody().getChildElements(), resultElementName);
            return (VLMResult)DefaultParser.getObjectMapper().readValue(resultJsonStr, VLMResult.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public SOAPMessage formatSoapString(String soapString) throws ClientException{
        try {
            MessageFactory msgFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = msgFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(soapString.getBytes(StringUtils.CHARSET_UTF_8)));
            soapMessage.saveChanges();
            return soapMessage;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String getElementValueByName(Iterator<SOAPElement> elements, String elementName) throws ClientException {
        try {
            if (elements.hasNext()) {
                SOAPElement element = elements.next();
                if (elementName.equals(element.getNodeName())){
                    return element.getValue();
                }
                Iterator childElements = element.getChildElements();
                return getElementValueByName(childElements, elementName);
            }
            return null;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
}
