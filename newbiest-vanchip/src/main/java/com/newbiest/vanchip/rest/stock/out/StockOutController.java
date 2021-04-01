package com.newbiest.vanchip.rest.stock.out;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.DocumentLine;
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

@RestController("VCStockOutController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class StockOutController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "物料批次发货", notes = "发货")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StockOutRequest")
    @RequestMapping(value = "/stockOut", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public StockOutResponse execute(@RequestBody StockOutRequest request) throws Exception {
        StockOutResponse response = new StockOutResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        StockOutResponseBody responseBody = new StockOutResponseBody();
        StockOutRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (StockOutRequest.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)){
            DocumentLine documentLine = requestBody.getDocumentLine();
            List<MaterialLot> materialLots = vanChipService.getWaitShipMLotByDocLine(documentLine);
            responseBody.setMaterialLots(materialLots);
        }else if (StockOutRequest.ACTION_TYPE_STOCK_OUT.equals(actionType)){
            vanChipService.stockOut(requestBody.getDocumentLine(), requestBody.getMaterialLotActionList());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
