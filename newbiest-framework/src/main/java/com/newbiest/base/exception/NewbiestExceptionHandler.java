package com.newbiest.base.exception;

import com.newbiest.msg.DefaultResponse;
import com.newbiest.msg.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 * Created by guoxunbo on 2017/12/31.
 */
@ControllerAdvice
@Slf4j
public class NewbiestExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleException(Exception e) {
        DefaultResponse response = new DefaultResponse();
        if (e instanceof ClientParameterException) {
            response.setParameters(((ClientParameterException) e).getParameters());
            response.setTransactionId(((ClientParameterException) e).getTransactionId());
            response = response.buildFailResponse(((ClientParameterException) e).getErrorCode());
        } else if (e instanceof ClientException) {
            response.setTransactionId(((ClientException) e).getTransactionId());
            response = response.buildFailResponse(((ClientException)e).getErrorCode());
        } else if (e instanceof MissingServletRequestParameterException) {
            log.error(e.getMessage(), e);
            // 处理参数未匹配异常返回正确的参数名称以及类型
            Object[] objects = {((MissingServletRequestParameterException) e).getParameterName(), ((MissingServletRequestParameterException) e).getParameterType()};
            response.setParameters(objects);
            response = response.buildFailResponse(NewbiestException.COMMON_SYSTEM_REQUEST_PARAMETER_ERROR);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            log.error(e.getMessage(), e);
            // 处理请求方式错误异常返回正确的请求方法
            response.setParameters(((HttpRequestMethodNotSupportedException) e).getSupportedMethods());
            response = response.buildFailResponse(NewbiestException.COMMON_SYSTEM_REQUEST_METHOD_ERROR);
        } else {
            log.error(e.getMessage(), e);
            response = response.buildFailResponse(NewbiestException.COMMON_SYSTEM_OCCURRED_ERROR);
        }
        return response;
    }

}
