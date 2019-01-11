package com.newbiest.base.rest.entity;

import com.fasterxml.jackson.databind.ObjectReader;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.msg.DefaultParser;
import com.newbiest.msg.Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对实体做增删改查 状态切换操作
 * Created by guoxunbo on 2018/7/26.
 */
@RestController
@RequestMapping("/framework")
@Slf4j
@Api(value="/framework", tags="FrameworkService", description = "系统一些通用功能")
public class EntityController extends AbstractRestController {

    @ApiOperation(value = "对实体做操作", notes = "Create, Update, Delete,GetByRrn,GetById等")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "EntityRequest")
    @RequestMapping(value = "/entityManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public EntityResponse execute(@RequestBody EntityRequest request) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        EntityResponse response = new EntityResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        EntityResponseBody responseBody = new EntityResponseBody();

        EntityRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        String entityModel = requestBody.getEntityModel();

        ClassLoader classLoader = ModelFactory.getModelClassLoader(entityModel);
        if (classLoader == null) {
            throw new ClientParameterException(NewbiestException.COMMON_MODEL_CLASS_LOADER_IS_NOT_EXIST, entityModel);
        }
        if (log.isDebugEnabled()) {
            log.debug("User [" + sc.getUsername() + "], actionType is ["+ actionType + "], modelClass is [" + entityModel + "]");
        }
        NBBase nbBase = parser(requestBody.getEntityString(), classLoader.loadClass(entityModel));
        if (EntityRequest.ACTION_CREATE.equals(actionType)) {
            nbBase = saveEntity(nbBase, sc);
        } else if (EntityRequest.ACTION_UPDATE.equals(actionType)) {
            nbBase = updateEntity(nbBase, sc);
        } else if (EntityRequest.ACTION_DELETE.equals(actionType)) {
            deleteEntity(nbBase, requestBody.getDeleteRelationEntityFlag(), sc);
        } else if (EntityRequest.ACTION_GET_BY_RRN.equals(actionType)) {
            nbBase = findEntity(nbBase);
            responseBody.setData(nbBase);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setData(nbBase);
        response.setBody(responseBody);
        return response;
    }


    private NBBase parser(String entityString, Class clazz) throws Exception{
        ObjectReader jsonReader = DefaultParser.getObjectMapper().readerFor(clazz);
        return jsonReader.readValue(entityString);
    }

}
