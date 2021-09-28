package com.newbiest.mms.rest.stock.in;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.MmsService;
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
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class StockInController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "物料批次入库", notes = "入库")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StockUpRequest")
    @RequestMapping(value = "/stockIn", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public StockInResponse execute(@RequestBody StockInRequest request) throws Exception {
        StockInResponse response = new StockInResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        StockInResponseBody responseBody = new StockInResponseBody();

        StockInRequestBody requestBody = request.getBody();
        List<MaterialLot> materialLots = requestBody.getMaterialLots();

        materialLots = mmsService.stockIn(materialLots, requestBody.getMaterialLotActionList());
        responseBody.setMaterialLots(materialLots);
        response.setBody(responseBody);
        return response;
    }

}
