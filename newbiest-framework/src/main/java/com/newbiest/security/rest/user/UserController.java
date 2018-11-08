package com.newbiest.security.rest.user;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.EncryptionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.msg.Request;
import com.newbiest.security.exception.SecurityException;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/security")
@Slf4j
@Api(value="/security", tags="SecurityService", description = "安全管理比如用户，用户组，菜单等的管理")
public class UserController extends AbstractRestController {

    @ApiOperation(value = "对用户做操作", notes = "支持ChangePassword, RestPassword, GetAuthority, Login, Register")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "UserRequest")
    @RequestMapping(value = "/userManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public UserResponse execute(@RequestBody UserRequest request, HttpServletResponse servletResponse) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        UserResponse response = new UserResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        UserResponseBody responseBody = new UserResponseBody();

        UserRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        NBUser requestUser = requestBody.getUser();
        NBUser user = null;

        if (UserRequest.ACTION_CREATE.equals(actionType)) {
            securityService.saveUser(requestUser, sc);
        } else if (UserRequest.ACTION_LOGIN.equals(actionType)) {
            user = securityService.login(requestUser.getUsername(), requestUser.getPassword(), sc);
            servletResponse.setHeader(AUTHORITY_HEAD_NAME, user.getToken());
        } else {
            if (requestUser.getObjectRrn() != null) {
                user = securityService.getUserByObjectRrn(requestUser.getObjectRrn());
            } else if (!StringUtils.isNullOrEmpty(requestUser.getUsername()) && !UserRequest.ACTION_CREATE.equals(actionType)) {
                user = securityService.getUserByUsername(requestUser.getUsername());
            }
            if (user == null) {
                throw new ClientParameterException(SecurityException.SECURITY_USER_IS_NOT_EXIST, requestUser.getObjectRrn() != null ? requestUser.getObjectRrn() : requestUser.getUsername());
            }
            if (UserRequest.ACTION_UPDATE.equals(actionType)) {
                user.setDescription(requestUser.getDescription());
                user.setDepartment(requestUser.getDepartment());
                user.setEmail(requestUser.getEmail());
                user.setPhone(requestUser.getPhone());
                user.setSex(requestUser.getSex());
                user = securityService.saveUser(user, sc);
            } else if (UserRequest.ACTION_CHANGE_PASSWORD.equals(actionType)) {
                requestUser.setNewPassword(EncryptionUtils.md5Hex(requestUser.getNewPassword()));
                user = securityService.changePassword(user, requestUser.getPassword(), requestUser.getNewPassword(), sc);
            } else if (UserRequest.ACTION_GET_AUTHORITY.equals(actionType)) {
                List<NBAuthority> authorityList = securityService.getAuthorities(user.getObjectRrn());
                user.setAuthorities(authorityList);
            }  else if (UserRequest.ACTION_RESET_PASSWORD.equals(actionType)) {
                user = securityService.resetPassword(user, sc);
            } else if (UserRequest.ACTION_GET_BY_RRN.equals(actionType)) {
                user = securityService.getDeepUser(user.getObjectRrn(), true);
            } else if (UserRequest.ACTION_DELETE.equals(actionType)) {
                securityService.deleteUser(user);
                user = null;
            } else {
                throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
            }
        }
        responseBody.setUser(user);
        response.setBody(responseBody);
        return response;
    }

}
