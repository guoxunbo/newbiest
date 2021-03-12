package com.newbiest.gc.rest.scm.assign;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.ScmService;
import com.newbiest.msg.Request;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.service.SecurityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class AssignController {

    @Autowired
    ScmService scmService;

    @Autowired
    SecurityService securityService;

    @ApiOperation(value = "SCMReserved", notes = "SCM预留")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "AssignRequest")
    @RequestMapping(value = "/scmassign", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public AssignResponse execute(@RequestBody AssignRequest request) throws Exception {
        AssignResponse response = new AssignResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        AssignResponseBody responseBody = new AssignResponseBody();
        AssignRequestBody requestBody = request.getBody();

        String username = request.getHeader().getUsername();
        NBUser nbUser = securityService.getUserByUsername(username);
        if (nbUser == null) {
            nbUser = new NBUser();
            nbUser.setUsername(username);
            nbUser.setDescription(username);
            securityService.saveUser(nbUser);
        }
        String actionType = requestBody.getActionType();
        if (AssignRequest.ACTION_TYPE_ASSIGN.equals(actionType)) {
            scmService.scmAssign(requestBody.getLotId(), requestBody.getVendor(), requestBody.getPoId(), requestBody.getMaterialType(), requestBody.getRemarks());
        } else if(AssignRequest.ACTION_TYPE_UN_ASSIGN.equals(actionType)) {
            scmService.scmUnAssign(requestBody.getLotId());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
