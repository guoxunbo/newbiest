package com.newbiest.gc.rest.receive.wafer;

import com.newbiest.base.exception.ClientException;
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
 * Created by guoxunbo on 2019-08-21 13:15
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class WaferManagerController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "对完成品做操作", notes = "接收")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "WaferManagerRequest")
    @RequestMapping(value = "/waferManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public WaferManagerResponse execute(@RequestBody WaferManagerRequest request) throws Exception {
        WaferManagerResponse response = new WaferManagerResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        String actionType = request.getBody().getActionType();

        if (WaferManagerRequest.ACTION_TYPE_RECEIVE.equals(actionType)) {
            gcService.validationAndReceiveWafer(request.getBody().getDocumentLines(), request.getBody().getMaterialLotActions());
        } else if (WaferManagerRequest.ACTION_TYPE_RECEIVE.equals(actionType)) {

        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + request.getBody().getActionType());
        }
        return response;
    }
}
