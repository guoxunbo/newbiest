package com.newbiest.vanchip.rest.ship;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
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

import java.util.List;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class ShipOutController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "发货", notes = "发货")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RetryInterfaceRequest")
    @RequestMapping(value = "/shipOut", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ShipOutResponse execute(@RequestBody ShipOutRequest request) throws Exception {
        ShipOutResponse response = new ShipOutResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ShipOutResponseBody responseBody = new ShipOutResponseBody();
        ShipOutRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (ShipOutRequest.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)){
            List<MaterialLot> materialLots = vanChipService.getWaitShipMLotByDocLineId(requestBody.getDocLineId());
            responseBody.setMaterialLots(materialLots);
        }else if (ShipOutRequest.ACTION_TYPE_SHIP_OUT.equals(actionType)){
            vanChipService.shipOut(requestBody.getDocLineId(), requestBody.getMaterialLots());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
