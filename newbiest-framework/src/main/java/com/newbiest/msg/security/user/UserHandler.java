package com.newbiest.msg.security.user;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.NewbiestConfiguration;
import com.newbiest.msg.*;
import com.newbiest.msg.trans.AbstractTransHandler;
import com.newbiest.msg.trans.TransContext;
import com.newbiest.security.exception.SecurityException;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBUser;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * 用户相关接口处理类
 * Created by guoxunbo on 2017/9/29.
 */
public class UserHandler extends AbstractTransHandler {

    @Override
    public Response executeRequest(Request request, TransContext context) throws ClientException {
        context.setTransactionId(request.getHeader().getTransactionId());
        SessionContext sc = getSessionContext(request, context);

        UserResponse response = new UserResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        UserResponseBody responseBody = new UserResponseBody();

        UserRequestBody requestBody = (UserRequestBody)request.getBody();
        String actionType = requestBody.getActionType();
        NBUser requestUser = requestBody.getUser();
        NBUser user = null;

        if (requestUser.getObjectRrn() != null) {
            user = context.getUserRepository().getByObjectRrn(requestUser.getObjectRrn());
        } else if (!StringUtils.isNullOrEmpty(requestUser.getUsername()) && !UserRequest.ACTION_CREATE.equals(actionType)) {
            user = context.getUserRepository().getByUsername(requestUser.getUsername());
        }

        if (UserRequest.ACTION_CREATE.equals(actionType)) {
            user = new NBUser();
            user.setUsername(requestUser.getUsername());
            // 如果没设置密码。则生成随机的6位数
            if (StringUtils.isNullOrEmpty(requestUser.getPassword())) {
                requestUser.setPassword(getPassword());
            }
            // 对密码进行加密
//            requestUser.setPassword(EncryptionUtils.encode(requestUser.getPassword()));
            user.setPassword(requestUser.getPassword());
            user.setDescription(requestUser.getDescription());
            user.setDepartment(requestUser.getDepartment());
            user.setEmail(requestUser.getEmail());
            user.setPhone(requestUser.getPhone());
            user.setSex(requestUser.getSex());
            // 第一次登录是否需要修改密码
            if (NewbiestConfiguration.getFirstLoginChangePwd()) {
                user.setInValidFlag(false);
            } else {
                user.setInValidFlag(true);
            }
            if (requestUser.getPwdLife() != null) {
                user.setPwdLife(requestUser.getPwdLife());
                Date pwdExpiry = DateUtils.plus(new Date(), requestUser.getPwdLife().intValue(), ChronoUnit.DAYS);
                user.setPwdExpiry(pwdExpiry);
            }
            context.getUserRepository().save(user, sc);
        } else {
            if (user == null) {
                throw new ClientParameterException(SecurityException.COMMON_USER_IS_NOT_FOUND, requestUser.getUsername());
            }
            if (UserRequest.ACTION_UPDATE.equals(actionType)) {
                user.setDescription(requestUser.getDescription());
                user.setDepartment(requestUser.getDepartment());
                user.setEmail(requestUser.getEmail());
                user.setPhone(requestUser.getPhone());
                user.setSex(requestUser.getSex());
                context.getUserRepository().save(user, sc);
            } else if (UserRequest.ACTION_CHANGE_PASSWORD.equals(actionType)) {
//                requestUser.setNewPassword(EncryptionUtils.encode(requestUser.getNewPassword()));
                user = context.getUserRepository().changePassword(user, requestUser.getPassword(), requestUser.getNewPassword(), sc);
            } else if (UserRequest.ACTION_GET_BY_RRN.equals(actionType)) {
                user = context.getUserRepository().getDeepUser(user.getObjectRrn(), true);
            } else if (UserRequest.ACTION_RESET_PASSWORD.equals(actionType)) {
                String newPassword = getPassword();
                user = context.getUserRepository().resetPassword(user, newPassword, sc);
            } else if (UserRequest.ACTION_GET_AUTHORITY.equals(actionType)) {
                List<NBAuthority> authorityList = context.getUserRepository().getTreeAuthorities(user.getObjectRrn());
                user.setAuthorities(authorityList);
            } else {
                throw new ClientException(Request.UN_SUPPORT_ACTION_TYPE + requestBody.getActionType());
            }
        }
        responseBody.setUser(user);
        response.setBody(responseBody);
        return response;
    }

    @Override
    public void initMessageParser() {
        MessageParserModel parserModel = new MessageParserModel();
        parserModel.setMessageName(UserRequest.MESSAGE_NAME);
        parserModel.setRequestClass(UserRequest.class);
        parserModel.setResponseClass(UserResponse.class);
        MessageParserFactory.registerMessageParser(UserRequest.MESSAGE_NAME, parserModel);
    }

    @Override
    public MessageParser getMessageParser() {
        return getMessageParser(UserRequest.MESSAGE_NAME);
    }

    @Override
    protected TransContext internalExecute(TransContext context) throws Exception {
        MessageParser parser = getMessageParser(UserRequest.MESSAGE_NAME);
        UserRequest request = (UserRequest)parser.readRequest(context.getRequest());
        UserResponse response = (UserResponse)executeRequest(request, context);

        context.setResponse(parser.writeResponse(response));
        return context;
    }

    private String getPassword() {
        if (NewbiestConfiguration.PASSWORD_POLICY_RANDOM.equals(NewbiestConfiguration.getPwdPolicy())) {
            return String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        }
        return "111111";
    }
}
