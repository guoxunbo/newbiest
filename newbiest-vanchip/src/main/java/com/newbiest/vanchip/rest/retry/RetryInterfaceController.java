package com.newbiest.vanchip.rest.retry;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.vanchip.service.ErpService;
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
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class RetryInterfaceController extends AbstractRestController {

    @Autowired
    ErpService erpService;

    @ApiOperation(value = "接口重发")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RetryInterfaceRequest")
    @RequestMapping(value = "/retryInterface", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RetryInterfaceResponse execute(@RequestBody RetryInterfaceRequest request) throws Exception {
        RetryInterfaceResponse response = new RetryInterfaceResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        RetryInterfaceResponseBody responseBody = new RetryInterfaceResponseBody();
        RetryInterfaceRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (RetryInterfaceRequest.ACTION_RETRY_INTERFACE.equals(actionType)){
            erpService.retry(requestBody.getInterfaceFailList());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
