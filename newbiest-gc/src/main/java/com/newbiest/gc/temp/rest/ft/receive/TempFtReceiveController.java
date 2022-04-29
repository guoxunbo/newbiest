package com.newbiest.gc.temp.rest.ft.receive;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.scm.dto.TempFtVboxModel;
import com.newbiest.gc.service.TempFtService;
import com.newbiest.msg.Request;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.service.SecurityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class TempFtReceiveController {

    @Autowired
    TempFtService tempFtService;

    @Autowired
    SecurityService securityService;

    @ApiOperation(value = "ftReceive", notes = "SCM预留")
    @ApiImplicitParam(name = "request", value = "request", required = true, dataType = "FtReceiveRequest")
    @RequestMapping(value = "/ftReceive", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public TempFtReceiveResponse execute(@RequestBody TempFtReceiveRequest request) throws Exception {
        TempFtReceiveResponse response = new TempFtReceiveResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        TempFtReceiveResponseBody responseBody = new TempFtReceiveResponseBody();
        TempFtReceiveRequestBody requestBody = request.getBody();

        String username = request.getHeader().getUsername();
        NBUser nbUser = securityService.getUserByUsername(username);
        if (nbUser == null) {
            nbUser = new NBUser();
            nbUser.setUsername(username);
            nbUser.setDescription(username);
            securityService.saveUser(nbUser);
        }

        String actionType = requestBody.getActionType();
        if (TempFtReceiveRequest.ACTION_TYPE_RECEIVE.equals(actionType)) {
            List<TempFtVboxModel> tempFtVboxModelList = requestBody.getTempFtVboxModels();
            tempFtService.receiveFtOldSystemVbox(tempFtVboxModelList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}

