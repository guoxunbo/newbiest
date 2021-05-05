package com.newbiest.gc.rest.stockIn;

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

import java.util.List;

@RestController("GcStockInController")
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class StockInController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "StockIn", notes = "入库位")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StockInRequest")
    @RequestMapping(value = "/stockIn", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public StockInResponse execute(@RequestBody StockInRequest request) throws Exception {
        StockInResponse response = new StockInResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        StockInResponseBody responseBody = new StockInResponseBody();
        StockInRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (StockInRequest.ACTION_QUERY.equals(actionType)) {
            String materialLotId = requestBody.getMaterialLotId();
            MaterialLot materialLot = gcService.getMaterialLotByMaterialLotIdAndTableRrn(materialLotId, requestBody.getTableRrn());
            responseBody.setMaterialLot(materialLot);
        } else if (StockInRequest.ACTION_QUERY_WAFER.equals(actionType)) {
            String lotId = requestBody.getLotId();
            MaterialLot materialLot = gcService.getWaitStockInStorageWaferByLotId(lotId, requestBody.getTableRrn());
            responseBody.setMaterialLot(materialLot);
        } else if (StockInRequest.ACTION_STOCK_IN.equals(actionType)) {
            gcService.stockIn(requestBody.getStockInModels());
        } else if(StockInRequest.ACTION_QUERY_MATERIAL.equals(actionType)){
            List<MaterialLot> materialLotList = gcService.queryRawMaterialByMaterialLotOrLotIdAndTableRrn(requestBody.getMaterialLotId(), requestBody.getTableRrn());
            responseBody.setMaterialLotList(materialLotList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
