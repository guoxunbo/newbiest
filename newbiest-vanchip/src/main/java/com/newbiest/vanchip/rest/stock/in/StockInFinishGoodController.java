package com.newbiest.vanchip.rest.stock.in;

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
public class StockInFinishGoodController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "物料批次入库", notes = "成品入库")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StockInFinishGoodRequest")
    @RequestMapping(value = "/stockInFinishGood", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public StockInFinishGoodResponse execute(@RequestBody StockInFinishGoodRequest request) throws Exception {
        StockInFinishGoodResponse response = new StockInFinishGoodResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        StockInFinishGoodResponseBody responseBody = new StockInFinishGoodResponseBody();

        StockInFinishGoodRequestBody requestBody = request.getBody();
        List<MaterialLot> materialLots = requestBody.getMaterialLots();

        materialLots = vanChipService.stockInFinishGood(materialLots, requestBody.getMaterialLotActionList());
        responseBody.setMaterialLots(materialLots);
        response.setBody(responseBody);
        return response;
    }

}
