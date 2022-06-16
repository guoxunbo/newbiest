package com.newbiest.gc.rest.hongkong.warehouse.manager;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.ThreeSideShipService;
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

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class HKWarehouseController {

    @Autowired
    GcService gcService;

    @Autowired
    ThreeSideShipService threeSideShipService;

    @ApiOperation(value = "HKWarehouseManger", notes = "香港仓管理")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "HKWareshouseRequest")
    @RequestMapping(value = "/HKWarehouseManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public HKWarehouseResponse execute(@RequestBody HKWarehouseRequest request) throws Exception {
        HKWarehouseResponse response = new HKWarehouseResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        HKWarehouseResponseBody responseBody = new HKWarehouseResponseBody();
        HKWarehouseRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (HKWarehouseRequest.ACTION_QUERY_MLOT.equals(actionType)) {
            MaterialLot materialLot = gcService.getHKWarehouseStockOutMLot(requestBody.getTableRrn(), requestBody.getQueryLotId());
            responseBody.setMaterialLot(materialLot);
        } else if(HKWarehouseRequest.ACTION_VALIDATE_HK_MLOT.equals(actionType)){
            boolean falg = gcService.validationHKStockOutMaterialLot(requestBody.getQueryMaterialLot(), requestBody.getMaterialLotActions());
            responseBody.setFalg(falg);
        } else if(HKWarehouseRequest.ACTION_HK_STOCK_OUT.equals(actionType)){
            gcService.wltStockOut(requestBody.getDocumentLines(), requestBody.getMaterialLotActions(), "", actionType);
        } else if(HKWarehouseRequest.ACTION_HK_BYORDER_STOCK_OUT.equals(actionType)) {
            threeSideShipService.ftRwMLotSaleShip(requestBody.getDocumentLines(), requestBody.getMaterialLotActions(), MaterialLot.HKWAREHOUSE_BY_ORDER_STOCK_OUT_RULE_ID);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
