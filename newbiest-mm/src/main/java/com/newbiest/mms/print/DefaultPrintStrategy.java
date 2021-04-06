package com.newbiest.mms.print;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.LabelTemplate;
import com.newbiest.mms.model.LabelTemplateParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.http.impl.client.HttpClientBuilder.create;

/**
 * @author guoxunbo
 * @date 4/6/21 3:07 PM
 */
@Component
@Slf4j
public class DefaultPrintStrategy implements IPrintStrategy {

    public static final int CONNECTION_TIME_OUT = 10;

    public static final int READ_TIME_OUT = 30;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        CloseableHttpClient client = createHttpClient().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        requestFactory.setConnectTimeout(CONNECTION_TIME_OUT * 1000);
        requestFactory.setReadTimeout(READ_TIME_OUT * 1000);
        restTemplate = new RestTemplate(requestFactory);
    }

    protected HttpClientBuilder createHttpClient() {
        return create().useSystemProperties().disableRedirectHandling().disableCookieManagement();
    }

    @Override
    public void print(PrintContext printContext) {
        LabelTemplate labelTemplate = printContext.getLabelTemplate();
        if (LabelTemplate.TYPE_BARTENDER.equals(labelTemplate.getType())) {
            printWithBartender(printContext);
        } else if (LabelTemplate.TYPE_EXCEL.equals(labelTemplate.getType())){
            //TODO 后续实现
        } else {
            throw new ClientParameterException(MmsException.MM_LBL_TEMPLATE_TYPE_IS_NOT_ALLOW, labelTemplate.getType());
        }
    }

    public Map<String, Object> buildParameters(PrintContext printContext) {
        Map<String, Object> parameterMap = Maps.newHashMap();
        List<LabelTemplateParameter> labelTemplateParameters = printContext.getLabelTemplate().getLabelTemplateParameterList();
        if (CollectionUtils.isNotEmpty(labelTemplateParameters)) {
            for (LabelTemplateParameter parameter : labelTemplateParameters) {
                Object value = null;
                if (printContext.getParameterMap().containsKey(parameter.getName())) {
                    value = parameterMap.get(parameter.getName());
                } else {
                    try {
                        value = PropertyUtils.getProperty(printContext.getBaseObject(), parameter.getName());
                        if (value != null) {
                            if (value instanceof Date) {
                                SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
                                value = sdf.format(value);
                            }
                            value = String.valueOf(value);
                        } else {
                            value = parameter.getDefaultValue();
                        }
                    } catch (Exception e) {
                        // 此处异常不处理
                        log.warn(e.getMessage(), e);
                    }
                }
                parameterMap.put(parameter.getName(), value);
            }
        }
        parameterMap.put("printCount", printContext.getLabelTemplate().getPrintCount());
        return parameterMap;
    }

    public void printWithBartender(PrintContext printContext) {
        String destination = printContext.getLabelTemplate().getBartenderDestination();
        Map<String, Object> params = buildParameters(printContext);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        if (log.isDebugEnabled()) {
            log.debug("Start to send print data to bartender. The destination is [ " + destination + "] and the parameter is [ " + params + "] ");
        }
        HttpEntity<byte[]> responseEntity = restTemplate.exchange(destination, HttpMethod.GET, entity, byte[].class, params);
        String response = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
        if (log.isDebugEnabled()) {
            log.debug(String.format("Get response from bartender. Response is [%s]", response));
        }
    }
}