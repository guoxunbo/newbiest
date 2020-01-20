package com.newbiest.gc.rest.async;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
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

/**
 * GlaxyCore 对物料批次的批量操作客制化
 * 中转箱，备货等等
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcAsyncController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "同步数据", notes = "同步发货单，重测发料单，")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcAsyncRequest")
    @RequestMapping(value = "/asyncManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcAsyncResponse execute(@RequestBody GcAsyncRequest request) throws Exception {
        GcAsyncResponse response = new GcAsyncResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcAsyncResponseBody responseBody = new GcAsyncResponseBody();

        GcAsyncRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (GcAsyncRequest.ACTION_ASYNC_RETEST_ISSUE_ORDER.equals(actionType)) {
            gcService.asyncReTestIssueOrder();
        } else if (GcAsyncRequest.ACTION_ASYNC_WAFER_ISSUE_ORDER.equals(actionType)) {
            gcService.asyncWaferIssueOrder();
        } else if (GcAsyncRequest.ACTION_ASYNC_RECEIVE_ORDER.equals(actionType)) {
            gcService.asyncReceiveOrder();
        } else if (GcAsyncRequest.ACTION_ASYNC_SHIP_ORDER.equals(actionType)) {
            gcService.asyncShipOrder();
        } else if(GcAsyncRequest.ACTION_ASYNC_PRODUCT.equals(actionType)){
            gcService.asyncMesProduct();
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
