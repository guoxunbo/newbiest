package com.newbiest.gc.rest.stockout;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.ThreeSideShipService;
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
public class StockOutController {

    @Autowired
    GcService gcService;

    @Autowired
    ThreeSideShipService threeSideShipService;

    @ApiOperation(value = "StockOut", notes = "发货")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StockOutRequest")
    @RequestMapping(value = "/stockOut", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public StockOutResponse execute(@RequestBody StockOutRequest request) throws Exception {
        StockOutResponse response = new StockOutResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        StockOutResponseBody responseBody = new StockOutResponseBody();
        StockOutRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (StockOutRequest.ACTION_STOCKOUT.equals(actionType)) {
            gcService.stockOut(requestBody.getDocumentLineList(), requestBody.getMaterialLotActions());
        } else if(StockOutRequest.ACTION_VALIDATION.equals(actionType)){
            boolean falg = gcService.validateStockOutMaterialLot(requestBody.getQueryMaterialLot(), requestBody.getMaterialLotActions());
            responseBody.setFalg(falg);
        } else if(StockOutRequest.ACTION_SALESHIP.equals(actionType)){
            threeSideShipService.comSaleShip(requestBody.getDocumentLineList(), requestBody.getMaterialLotActions());
        } else if(StockOutRequest.ACTION_TRANSFER_SHIP.equals(actionType)){
            gcService.transferShip(requestBody.getDocumentLineList(), requestBody.getMaterialLotActions(), requestBody.getWarehouseId());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
