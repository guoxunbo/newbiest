package com.newbiest.msg.base.entity;

import com.fasterxml.jackson.databind.ObjectReader;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.msg.*;
import com.newbiest.msg.trans.AbstractTransHandler;
import com.newbiest.msg.trans.TransContext;

/**
 * 普通对象的增删改查
 * Created by guoxunbo on 2017/9/29.
 */
public class EntityManagerHandler extends AbstractTransHandler {

    @Override
    public Response executeRequest(Request request, TransContext context) throws Exception {
        context.setTransactionId(request.getHeader().getTransactionId());
        SessionContext sc = getSessionContext(request, context);

        EntityManagerResponse response = new EntityManagerResponse();
        EntityManagerResponseBody responseBody = new EntityManagerResponseBody();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        EntityManagerRequestBody requestBody = (EntityManagerRequestBody)request.getBody();
        String actionType = requestBody.getActionType();
        String entityName = requestBody.getEntityName();

        Class modelClass = null;
        ClassLoader classLoader = ModelFactory.getModelClassLoader(entityName);
        if (classLoader != null) {
            modelClass = classLoader.loadClass(entityName);
        } else {
            throw new ClientParameterException(NewbiestException.COMMON_MODEL_CLASS_LOADER_IS_NULL, entityName);
        }
        String entityString = requestBody.getEntityString();
        NBBase nbBae = parser(entityString, modelClass);

        if (EntityManagerRequest.ACTION_CREATE.equals(actionType)) {
            nbBae = context.getNbManager().saveEntity(nbBae, sc);
        } else if (EntityManagerRequest.ACTION_UPDATE.equals(actionType)) {
            if (nbBae instanceof NBUpdatable) {
                NBUpdatable oldBase = (NBUpdatable) context.getNbManager().getEntity(nbBae, false);
                if (!oldBase.getLockVersion().equals(((NBUpdatable) nbBae).getLockVersion())) {
                    throw new ClientParameterException(NewbiestException.COMMON_OBJECT_IS_NOT_NEWEST, oldBase.getClass().getName(), oldBase.getLockVersion());
                }
            }
            nbBae = context.getNbManager().saveEntity(nbBae, sc);
        }  else if (EntityManagerRequest.ACTION_DELETE.equals(actionType)) {
            context.getNbManager().deleteEntity(nbBae, requestBody.getThrowExistRelationException(), sc);
        } else if (EntityManagerRequest.ACTION_GET_BY_RRN.equals(actionType)) {
            nbBae = context.getNbManager().getEntity(nbBae, true);
        } else {
            throw new ClientException(Request.UN_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setData(nbBae);
        response.setBody(responseBody);
        return response;
    }

    @Override
    public void initMessageParser() {
        MessageParserModel parserModel = new MessageParserModel();
        parserModel.setMessageName(EntityManagerRequest.MESSAGE_NAME);
        parserModel.setRequestClass(EntityManagerRequest.class);
        parserModel.setResponseClass(EntityManagerResponse.class);
        MessageParserFactory.registerMessageParser(EntityManagerRequest.MESSAGE_NAME, parserModel);
    }

    @Override
    public MessageParser getMessageParser() {
        return getMessageParser(EntityManagerRequest.MESSAGE_NAME);
    }

    @Override
    protected TransContext internalExecute(TransContext context) throws Exception {
        MessageParser parser = getMessageParser(EntityManagerRequest.MESSAGE_NAME);
        EntityManagerRequest request = (EntityManagerRequest)parser.readRequest(context.getRequest());
        EntityManagerResponse response = (EntityManagerResponse)executeRequest(request, context);

        context.setResponse(parser.writeResponse(response));
        return context;
    }

    private NBBase parser(String entityString, Class clazz) throws Exception{
        ObjectReader jsonReader = DefaultParser.getObjectMapper().readerFor(clazz);
        return jsonReader.readValue(entityString);
    }

}
