package com.newbiest.gc.rest.scm.assign;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.service.GcService;
import com.newbiest.msg.Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    GcService gcService;

    @ApiOperation(value = "SCMReserved", notes = "SCM预留")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "AssignRequest")
    @RequestMapping(value = "/scmassign", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public AssignResponse execute(@RequestBody AssignRequest request) throws Exception {
        AssignResponse response = new AssignResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        AssignResponseBody responseBody = new AssignResponseBody();
        AssignRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        String unitId = requestBody.getLotId() + StringUtils.SPLIT_CODE + requestBody.getWaferId();
        if (AssignRequest.ACTION_TYPE_ASSIGN.equals(actionType)) {
            //TODO 处理SCM标记逻辑
        } else if(AssignRequest.ACTION_TYPE_UN_ASSIGN.equals(actionType)){
            //TODO 处理SCM取消标记逻辑
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
