package com.newbiest.msg.trans;

import com.newbiest.base.exception.ClientException;
import com.newbiest.msg.MessageParser;
import com.newbiest.msg.Request;
import com.newbiest.msg.Response;

/**
 * 消息处理的基本接口
 * Created by guoxunbo on 2017/9/29.
 */
public interface ITransHandler {

    public String execute(TransContext context) throws Exception;

    public Response executeRequest(Request request, TransContext context) throws Exception;

    public Object executeResponse(Response response, TransContext context) throws Exception;

    public void initMessageParser();

    public MessageParser getMessageParser();
}
