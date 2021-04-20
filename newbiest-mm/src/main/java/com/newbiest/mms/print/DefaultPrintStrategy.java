package com.newbiest.mms.print;

import com.google.common.collect.Lists;
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
@Component(DefaultPrintStrategy.DEFAULT_STRATEGY_NAME)
@Slf4j
public class DefaultPrintStrategy implements IPrintStrategy {

    public static final String DEFAULT_STRATEGY_NAME = "defaultPrintStrategy";

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
        Map<String, Object> parameterMap = printContext.getParameterMap();
        List<LabelTemplateParameter> labelTemplateParameters = printContext.getLabelTemplate().getLabelTemplateParameterList();
        if (CollectionUtils.isNotEmpty(labelTemplateParameters)) {
            for (LabelTemplateParameter parameter : labelTemplateParameters) {
                Object value = null;
                if (printContext.getParameterMap().containsKey(parameter.getName())) {
                    value = printContext.getParameterMap().get(parameter.getName());
                } else {
                    try {
                        value = PropertyUtils.getProperty(printContext.getBaseObject(), parameter.getName());
                    } catch (Exception e) {
                        // 此处异常不处理
                        log.warn(e.getMessage(), e);
                    }
                }
                if (value == null) {
                    value = parameter.getDefaultValue();
                }
                parameterMap.put(parameter.getName(), value);
                log.debug("parameterName:" + parameter.getName() + ".value :" + value);
            }
        }

        if (parameterMap != null && parameterMap.size() > 0) {
            for (String key : parameterMap.keySet()) {
                Object value = parameterMap.get(key);
                if (value != null && value instanceof Date) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
                    value = sdf.format(value);
                    parameterMap.put(key, value);
                }
            }
        }
        parameterMap.put("printCount", printContext.getLabelTemplate().getPrintCount());
        return parameterMap;
    }

    public void printWithBartender(PrintContext printContext) {
        String destination = printContext.getLabelTemplate().getBartenderDestination(printContext.getWorkStation());
        Map<String, Object> params = buildParameters(printContext);

        List<String> paramStr = Lists.newArrayList();
        for (String key : params.keySet()) {
            paramStr.add(key + "=" + params.get(key));
        }

        destination = destination + "?" + StringUtils.join(paramStr, "&");
        if (log.isDebugEnabled()) {
            log.debug("Start to send print data to bartender. The destination is [ " + destination + "] ");
        }

        HttpEntity<byte[]> responseEntity = restTemplate.getForEntity(destination, byte[].class);
        String response = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());
        if (log.isDebugEnabled()) {
            log.debug(String.format("Get response from bartender. Response is [%s]", response));
        }
    }
}
