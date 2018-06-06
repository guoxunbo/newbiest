package com.newbiest.msg.security.role;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.NewbiestConfiguration;
import com.newbiest.msg.*;
import com.newbiest.msg.trans.AbstractTransHandler;
import com.newbiest.msg.trans.TransContext;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBUser;

import java.util.List;

/**
 * UserRole相关接口处理类
 * Created by guoxunbo on 2017/9/29.
 */
public class RoleHandler extends AbstractTransHandler {

    @Override
    public Response executeRequest(Request request, TransContext context) throws ClientException {
        context.setTransactionId(request.getHeader().getTransactionId());
        SessionContext sc = getSessionContext(request, context);

        RoleResponse response = new RoleResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        RoleResponseBody responseBody = new RoleResponseBody();

        RoleRequestBody requestBody = (RoleRequestBody)request.getBody();
        String actionType = requestBody.getActionType();
        NBRole requestRole = requestBody.getNbRole();
        NBRole role = null;

        if (requestRole.getObjectRrn() != null) {
            role = context.getRoleRepository().getByObjectRrn(requestRole.getObjectRrn());
        } else if (!StringUtils.isNullOrEmpty(requestRole.getRoleId()) && !RoleRequest.ACTION_CREATE.equals(actionType)) {
            role = context.getRoleRepository().getByRoleId(requestRole.getRoleId());
        }

        if (RoleRequest.ACTION_CREATE.equals(actionType)) {
            role = new NBRole();
            PropertyUtils.copyProperties(requestRole, role);
            role = context.getRoleRepository().save(role);
        } else {
            if (role == null) {
                throw new ClientParameterException(NewbiestException.COMMON_ROLE_IS_NULL, requestRole.getRoleId());
            }
            if (!RoleRequest.ACTION_DELETE.equals(actionType)) {
                role = context.getRoleRepository().getDeepRole(role.getObjectRrn(), true, sc);
                List<NBUser> users = role.getUsers();
                List<NBAuthority> authorities = role.getAuthorities();
                if (RoleRequest.ACTION_UPDATE.equals(actionType) ) {
                    // 更新的话不能更新用户和权限 只能通过分配来进行分配
                    role.setDescription(requestRole.getDescription());
                    role.setUsers(users);
                    role.setAuthorities(authorities);
                } else if (RoleRequest.ACTION_GET_BY_ID.equals(actionType) ||
                        RoleRequest.ACTION_GET_BY_RRN.equals(actionType)) {
                    // do nothing 因为role已经在上面查询了
                } else if (RoleRequest.DISPATCH_AUTHORITY.equals(actionType)) {
                    // 分配相关不支持修改信息
                    role.setUsers(users);
                    role.setAuthorities(requestRole.getAuthorities());
                } else if (RoleRequest.DISPATCH_USER.equals(actionType)) {
                    role.setUsers(requestRole.getUsers());
                    role.setAuthorities(authorities);
                } else if (RoleRequest.DISPATCH_ALL.equals(actionType)) {
                    role.setUsers(requestRole.getUsers());
                    role.setAuthorities(requestRole.getAuthorities());
                } else {
                    throw new ClientException(Request.UN_SUPPORT_ACTION_TYPE + requestBody.getActionType());
                }

                if (!(RoleRequest.ACTION_GET_BY_ID.equals(actionType) ||
                        RoleRequest.ACTION_GET_BY_RRN.equals(actionType))) {
                    role = context.getRoleRepository().save(role);
                }
            } else {
                context.getNbManager().deleteEntity(role, true, sc);
            }
        }
        responseBody.setNbRole(role);
        response.setBody(responseBody);
        return response;
    }

    @Override
    public void initMessageParser() {
        MessageParserModel parserModel = new MessageParserModel();
        parserModel.setMessageName(RoleRequest.MESSAGE_NAME);
        parserModel.setRequestClass(RoleRequest.class);
        parserModel.setResponseClass(RoleResponse.class);
        MessageParserFactory.registerMessageParser(RoleRequest.MESSAGE_NAME, parserModel);
    }

    @Override
    public MessageParser getMessageParser() {
        return getMessageParser(RoleRequest.MESSAGE_NAME);
    }

    @Override
    protected TransContext internalExecute(TransContext context) throws Exception {
        MessageParser parser = getMessageParser(RoleRequest.MESSAGE_NAME);
        RoleRequest request = (RoleRequest)parser.readRequest(context.getRequest());
        RoleResponse response = (RoleResponse)executeRequest(request, context);

        context.setResponse(parser.writeResponse(response));
        return context;
    }

}
