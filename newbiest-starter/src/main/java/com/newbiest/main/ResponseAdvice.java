package com.newbiest.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.newbiest.base.filter.NewbiestFilter;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.msg.DefaultParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.concurrent.TimeUnit;


/**
 * 记录response值
 * Created by guoxunbo on 2019-12-13 11:35
 */
@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object>{

    Logger logger = LoggerFactory.getLogger(NewbiestFilter.class.getName());

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 不拦截druid相关请求
        ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) serverHttpRequest;
        String requestUrl = servletServerHttpRequest.getURI().toString();
        if (requestUrl.contains("druid") || requestUrl.contains("actuator") ||
                requestUrl.contains("swagger") || requestUrl.contains("api-doc")) {
            return o;
        }

        StringBuffer logBuffer = new StringBuffer();
        logBuffer.append("The TransactionId [" + ThreadLocalContext.getTransRrn() + "] sent response.");
        if (logger.isDebugEnabled()) {
            ObjectMapper objectMapper = DefaultParser.getObjectMapper();
            try {
                logBuffer.append("The responseJson is [" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o) + "]");
                logger.debug(logBuffer.toString());
            } catch (JsonProcessingException e) {
                logBuffer.append("UnSerializable response failed. Response Obj toString is [" + o.toString() + "]");
                logger.warn(logBuffer.toString());
            }
        } else if (!logger.isDebugEnabled() && logger.isInfoEnabled()) {
            logger.info(logBuffer.toString());
        }
        return o;
    }
}
