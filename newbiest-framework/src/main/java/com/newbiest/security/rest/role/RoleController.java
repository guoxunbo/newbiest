package com.newbiest.security.rest.role;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.msg.Request;
import com.newbiest.security.exception.SecurityException;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/security")
@Slf4j
@Api(value="/security", tags="SecurityService", description = "安全管理比如用户，用户组，菜单等的管理")
public class RoleController extends AbstractRestController {

    @ApiOperation(value = "对用户组做操作", notes = "支持DispatchUser, DispatchAuthority, DispatchAll等")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RoleRequest")
    @RequestMapping(value = "/roleManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RoleResponse execute(@RequestBody RoleRequest request) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        RoleResponse response = new RoleResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        RoleResponseBody responseBody = new RoleResponseBody();

        RoleRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        NBRole requestRole = requestBody.getRole();
        NBRole role = null;

        if (RoleRequest.ACTION_CREATE.equals(actionType)) {
            role = new NBRole();
            PropertyUtils.copyProperties(requestRole, role);
            role = securityService.saveRole(role);
        } else {
            if (requestRole.getObjectRrn() != null) {
                role = securityService.getRoleByObjectRrn(requestRole.getObjectRrn());
            } else if (!StringUtils.isNullOrEmpty(requestRole.getRoleId()) && !RoleRequest.ACTION_CREATE.equals(actionType)) {
                role = securityService.getRoleByRoleId(requestRole.getRoleId());
            }
            if (role == null) {
                throw new ClientParameterException(SecurityException.SECURITY_ROLE_IS_NOT_EXIST, requestRole.getObjectRrn() != null ? requestRole.getObjectRrn() : requestRole.getRoleId());
            }
            if (!RoleRequest.ACTION_DELETE.equals(actionType)) {
                role = securityService.getDeepRole(role.getObjectRrn(), true, sc);
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
                    throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
                }
                if (!(RoleRequest.ACTION_GET_BY_ID.equals(actionType) || RoleRequest.ACTION_GET_BY_RRN.equals(actionType))) {
                    role = securityService.saveRole(role);
                }
            } else {
                securityService.deleteRole(role);
                role = null;
            }
        }

        responseBody.setRole(role);
        response.setBody(responseBody);
        return response;
    }

}
