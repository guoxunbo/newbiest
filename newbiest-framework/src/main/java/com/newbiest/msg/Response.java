package com.newbiest.msg;

import lombok.Data;

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
