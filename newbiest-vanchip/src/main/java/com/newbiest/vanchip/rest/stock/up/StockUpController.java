package com.newbiest.vanchip.rest.stock.up;

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
@Api(value="/vc", tags="MaterialManagerSystem", description = "备货")
public class StockUpController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "物料批次备货", notes = "备货")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StockUpRequest")
    @RequestMapping(value = "/stockUp", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public StockUpResponse execute(@RequestBody StockUpRequest request) throws Exception {
        StockUpResponse response = new StockUpResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        StockUpResponseBody responseBody = new StockUpResponseBody();

        StockUpRequestBody requestBody = request.getBody();
        List<MaterialLot> materialLots = requestBody.getMaterialLots();
        String docLineId = requestBody.getDocLineId();
        String actionType = requestBody.getActionType();
        if (StockUpRequest.ACTION_GET_MATERIAL_LOT.equals(actionType)) {
            List<MaterialLot> materialLotList = vanChipService.getReservedMLotByOrder(docLineId);
            responseBody.setMaterialLotList(materialLotList);
        }else if (StockUpRequest.ACTION_SOTCK_UP_MLOT.equals(actionType)){
            vanChipService.stockUpMLot(docLineId, materialLots);
        }
        response.setBody(responseBody);
        return response;
    }

}
