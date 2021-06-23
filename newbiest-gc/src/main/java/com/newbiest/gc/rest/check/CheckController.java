package com.newbiest.gc.rest.check;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
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
public class CheckController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "CheckInventory", notes = "盘点")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "CheckRequest")
    @RequestMapping(value = "/checkInventory", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public CheckResponse execute(@RequestBody CheckRequest request) throws Exception {
        CheckResponse response = new CheckResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        CheckResponseBody responseBody = new CheckResponseBody();
        CheckRequestBody requestBody = request.getBody();
        gcService.checkMaterialInventory(requestBody.getExistMaterialLots(), requestBody.getErrorMaterialLots());

        String actionType = requestBody.getActionType();
        if(CheckRequest.ACTION_CHECK.equals(actionType)){
            gcService.checkMaterialInventory(requestBody.getExistMaterialLots(), requestBody.getErrorMaterialLots());
        } else if (CheckRequest.ACTION_QUERY.equals(actionType)){
            MaterialLot materialLot = gcService.getMaterialLotByTableRrnAndMaterialLotIdOrLotId(requestBody.getTableRrn(),requestBody.getQueryLotId());
            responseBody.setMaterialLot(materialLot);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
