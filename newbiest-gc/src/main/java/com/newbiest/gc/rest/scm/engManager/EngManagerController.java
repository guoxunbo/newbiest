package com.newbiest.gc.rest.scm.engManager;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.ScmService;
import com.newbiest.msg.Request;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.service.SecurityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value = "/gc", tags = "gc客制化接口", description = "GalaxyCore客制化接口")
public class EngManagerController {

    @Autowired
    ScmService scmService;

    @Autowired
    GcService gcService;

    @Autowired
    SecurityService securityService;

    @ApiImplicitParam(name = "request", value = "request", required = true, dataType = "EngManagerRequest")
    @RequestMapping(value = "/scmEngManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public EngManagerResponse execute(@RequestBody EngManagerRequest request) throws Exception {
        EngManagerResponse response = new EngManagerResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        EngManagerResponseBody responseBody = new EngManagerResponseBody();
        EngManagerRequestBody requestBody = request.getBody();

        String username = request.getHeader().getUsername();
        NBUser nbUser = securityService.getUserByUsername(username);
        if (nbUser == null) {
            nbUser = new NBUser();
            nbUser.setUsername(username);
            nbUser.setDescription(username);
            securityService.saveUser(nbUser);
        }

        log.info("lotEngInfoList is " + requestBody.getLotEngInfoList());
        String actionType = requestBody.getActionType();
        if (EngManagerRequest.ACTION_TYPE_SAVE.equals(actionType) || EngManagerRequest.ACTION_TYPE_UPDATE.equals(actionType)) {
            scmService.scmSaveEngInfo(requestBody.getLotEngInfoList(), actionType);
        } else if (EngManagerRequest.ACTION_TYPE_DELETE.equals(actionType)) {
            scmService.scmDeleteEngInfo(requestBody.getLotEngInfoList());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }
}
