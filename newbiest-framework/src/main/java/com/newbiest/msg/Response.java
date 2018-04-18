package com.newbiest.msg;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiResponse;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * 所有请求的回复的基类
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class Response implements Serializable {

    private ResponseHeader header;

    public Response() {
        header = new ResponseHeader();
        header.setResult(ResponseHeader.RESULT_SUCCESS);
    }

    public Response(String transactionId) {
        header = new ResponseHeader();
        header.setTransactionId(transactionId);
        header.setResult(ResponseHeader.RESULT_SUCCESS);
    }

}
