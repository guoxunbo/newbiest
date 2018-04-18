package com.newbiest.base.exception;


import com.newbiest.base.utils.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

/**
 * 所有异常的基类
 * Created by guoxunbo on 2017/9/16.
 */
public class ClientException extends RuntimeException {

    /**
     * 当违反唯一约束时会出现的字符
     */
    private static final String DUPLICATIE_ENTRY = "Duplicate entry";
    /**
     * sqlMessage里面索引名称前缀字符
     */
    private static final String FOR_KEY = "for key";

    private String errorCode;

    private String transactionId;

    public ClientException() {
    }

    public ClientException(String errorCode) {
        this.errorCode = errorCode;
    }

    public ClientException(Throwable cause) {
        if (cause.getCause() != null) {
            if (cause.getCause() instanceof ConstraintViolationException) {
                String message = ((ConstraintViolationException) cause.getCause()).getSQLException().getMessage();
                if (!StringUtils.isNullOrEmpty(message) && message.contains(DUPLICATIE_ENTRY)) {
                    errorCode = "Value is exist :" + message.substring(DUPLICATIE_ENTRY.length(), message.indexOf(FOR_KEY));
                }
            } //TODO 处理其他异常
        } else {
            errorCode = cause.getMessage();
        }

    }

    @Override
    public String toString() {
        if (!StringUtils.isNullOrEmpty(errorCode)) {
            return errorCode;
        }
        return super.toString();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
