package com.newbiest.vanchip.rest.async;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.vanchip.service.VanChipService;
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
@Api(value="vc", tags="vc客制化接口", description = "VanChip客制化")
public class VcAsyncController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "同步数据", notes = "同步成品型号")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "VcAsyncRequest")
    @RequestMapping(value = "/asyncManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public VcAsyncResponse execute(@RequestBody VcAsyncRequest request) throws Exception {
        VcAsyncResponse response = new VcAsyncResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        VcAsyncResponseBody responseBody = new VcAsyncResponseBody();

        VcAsyncRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        if (VcAsyncRequest.ACTION_ASYNC_PRODUCT.equals(actionType)) {
            vanChipService.asyncMesProduct();
        }  else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
