package com.newbiest.gc.rest.stockout.wltStockout;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.rest.stockout.StockOutRequest;
import com.newbiest.gc.rest.stockout.StockOutRequestBody;
import com.newbiest.gc.rest.stockout.StockOutResponse;
import com.newbiest.gc.rest.stockout.StockOutResponseBody;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
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

@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class WltStockOutController {

    @Autowired
    GcService gcService;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @ApiOperation(value = "WltStockOut", notes = "Wlt/CP发货")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "WltStockOutRequest")
    @RequestMapping(value = "/wltStockOut", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public WltStockOutResponse execute(@RequestBody WltStockOutRequest request) throws Exception {
        WltStockOutResponse response = new WltStockOutResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        WltStockOutResponseBody responseBody = new WltStockOutResponseBody();
        WltStockOutRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (WltStockOutRequest.ACTION_WLTSTOCKOUT.equals(actionType)) {
            gcService.wltStockOut(requestBody.getDocumentLines(), requestBody.getMaterialLotActions(), requestBody.getCheckSubCode());
        } else if(WltStockOutRequest.ACTION_WLTOTHERSTOCKOUT.equals(actionType)) {
            gcService.wltOtherStockOut(requestBody.getDocumentLines(), requestBody.getMaterialLotActions());
        } else if(WltStockOutRequest.ACTION_VALIDATION_WLTMLOT.equals(actionType)){
            boolean falg = gcService.validateMLotByPackageRule(requestBody.getQueryMaterialLot(), requestBody.getMaterialLotActions());
            responseBody.setFalg(falg);
        } else if(WltStockOutRequest.ACTION_QUERY_STOCKOUTTAG_MLOTUNIT.equals(actionType)){
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.queryStockOutTagMLotUnits(requestBody.getMaterialLotActions());
            responseBody.setMaterialLotUnitList(materialLotUnitList);
        } else if(WltStockOutRequest.ACTION_STOCKOUTTAG.equals(actionType)){
            gcService.waferStockOutTagging(requestBody.getMaterialLotActions(), requestBody.getStockTagNote(), requestBody.getCustomerName(), requestBody.getStockOutType(), requestBody.getPoId(),requestBody.getAddress());
        } else if(WltStockOutRequest.ACTION_UNSTOCKOUTTAG.equals(actionType)){
            gcService.waferUnStockOutTagging(requestBody.getMaterialLotActions());
        } else if(WltStockOutRequest.ACTION_VALIDATE_VENDER.equals(actionType)){
            gcService.validationMaterialLotVender(requestBody.getMaterialLotActions());
        } else if(WltStockOutRequest.ACTION_GETMLOT.equals(actionType)){
            MaterialLot materialLot = gcService.getMaterialLotByTableRrnAndMaterialLotIdOrLotId(requestBody.getTableRrn(),requestBody.getQueryLotId());
            responseBody.setMaterialLot(materialLot);
        } else if(WltStockOutRequest.ACTION_VALIDATE_MATERIAL_NAME.equals(actionType)){
            gcService.validationMLotMaterialName(requestBody.getMaterialLotActions());
        } else if(WltStockOutRequest.ACTION_THREESIDE_SHIP.equals(actionType)){
            gcService.wltCpThreeSideShip(requestBody.getDocumentLine(), requestBody.getMaterialLotActions());
        } else if(WltStockOutRequest.ACTION_SALE_SHIP.equals(actionType)) {
            gcService.wltCpMaterialLotSaleShip(requestBody.getDocumentLines(), requestBody.getMaterialLotActions(), requestBody.getCheckSubCode());
        } else if (WltStockOutRequest.ACTION_GC_RW_ATTRIBUTE_CHANGE.equals(actionType)){
            gcService.rWAttributeChange(requestBody.getMaterialLots());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
