package com.newbiest.msg.base.entitylist;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.msg.*;
import com.newbiest.msg.trans.AbstractTransHandler;
import com.newbiest.msg.trans.TransContext;

import java.util.List;

/**
 * 通用查询处理器
 * Created by guoxunbo on 2017/9/29.
 */
public class EntityListHandler extends AbstractTransHandler {

    @Override
    public Response executeRequest(Request request, TransContext context) throws ClientException {
        context.setTransactionId(request.getHeader().getTransactionId());
        SessionContext sc = getSessionContext(request, context);

        EntityListResponse response = new EntityListResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        EntityListRequestBody requestBody = (EntityListRequestBody)request.getBody();
        String entityModel = requestBody.getEntityModel();
        int firstResult = requestBody.getFirstResult();
        int maxResult = requestBody.getMaxResult();
        String orderBy = requestBody.getOrderBy();
        String whereClause = requestBody.getWhereClause();
        List<String> properties = requestBody.getFields();

        List<NBBase> dataList = context.getNbManager().getEntityListForFiled(sc.getOrgRrn(), entityModel, firstResult, maxResult, whereClause, orderBy, properties);
        EntityListResponseBody responseBody = new EntityListResponseBody();
        

        responseBody.setDatas(dataList);
        response.setBody(responseBody);
        return response;
    }

    @Override
    public void initMessageParser() {
        MessageParserModel parserModel = new MessageParserModel();
        parserModel.setMessageName(EntityListRequest.MESSAGE_NAME);
        parserModel.setRequestClass(EntityListRequest.class);
        parserModel.setResponseClass(EntityListResponse.class);
        MessageParserFactory.registerMessageParser(EntityListRequest.MESSAGE_NAME, parserModel);
    }

    @Override
    public MessageParser getMessageParser() {
        return getMessageParser(EntityListRequest.MESSAGE_NAME);
    }

    @Override
    protected TransContext internalExecute(TransContext context) throws Exception {
        MessageParser parser = getMessageParser(EntityListRequest.MESSAGE_NAME);
        EntityListRequest request = (EntityListRequest)parser.readRequest(context.getRequest());
        EntityListResponse response = (EntityListResponse)executeRequest(request, context);

        context.setResponse(parser.writeResponse(response));
        return context;
    }

}
