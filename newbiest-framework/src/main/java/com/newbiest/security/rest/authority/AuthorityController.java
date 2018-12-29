package com.newbiest.security.rest.authority;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.msg.Request;
import com.newbiest.security.model.NBAuthority;
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
public class AuthorityController extends AbstractRestController {

    @ApiOperation(value = "对菜单做操作", notes = "支持GET_AUTHORITY_TREE")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "AuthorityRequest")
    @RequestMapping(value = "/authorityManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public AuthorityResponse execute(@RequestBody AuthorityRequest request) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        AuthorityResponse response = new AuthorityResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        AuthorityResponseBody responseBody = new AuthorityResponseBody();


        AuthorityRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (AuthorityRequest.GET_AUTHORITY_TREE.equals(actionType)) {
            List<NBAuthority> authorities = securityService.getTreeAuthorities();
            responseBody.setAuthorities(authorities);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }

}
