package com.newbiest.gc.rest.scm.hold;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.ScmService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.Request;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.service.SecurityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class HoldController {

    @Autowired
    ScmService scmService;

    @Autowired
    SecurityService securityService;

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "HoldRequest")
    @RequestMapping(value = "/scmhold", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public HoldResponse execute(@RequestBody HoldRequest request) throws Exception {
        HoldResponse response = new HoldResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        HoldResponseBody responseBody = new HoldResponseBody();
        HoldRequestBody requestBody = request.getBody();

        String username = request.getHeader().getUsername();
        NBUser nbUser = securityService.getUserByUsername(username);
        if (nbUser == null) {
            nbUser = new NBUser();
            nbUser.setUsername(username);
            nbUser.setDescription(username);
            securityService.saveUser(nbUser);
        }
        String actionType = requestBody.getActionType();
        List<String> materialLotIds = requestBody.getMaterialLotList().stream().map(MaterialLot:: getMaterialLotId).collect(Collectors.toList());
        if (HoldRequest.ACTION_TYPE_HOLD.equals(actionType)) {
            scmService.scmHold(materialLotIds, requestBody.getActionCode(), requestBody.getActionReason(), requestBody.getActionRemarks());
        } else if(HoldRequest.ACTION_TYPE_RELEASE.equals(actionType)) {
            scmService.scmRelease(materialLotIds, requestBody.getActionCode(), requestBody.getActionReason(), requestBody.getActionRemarks());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
