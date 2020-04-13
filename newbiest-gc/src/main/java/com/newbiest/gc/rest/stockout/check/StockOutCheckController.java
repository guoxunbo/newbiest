package com.newbiest.gc.rest.stockout.check;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.gc.model.StockOutCheck;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.MmsService;
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
import java.util.stream.Collectors;

/**
 * 出货前检验
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class StockOutCheckController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "出货前检验")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StockOutCheckRequest")
    @RequestMapping(value = "/stockOutCheck", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public StockOutCheckResponse execute(@RequestBody StockOutCheckRequest request) throws Exception {
        StockOutCheckResponse response = new StockOutCheckResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        StockOutCheckResponseBody responseBody = new StockOutCheckResponseBody();

        StockOutCheckRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (StockOutCheckRequest.ACTION_GET_CHECK_LIST.equals(actionType)) {
            List<NBOwnerReferenceList> stockOutChecks = gcService.getStockOutCheckList();
            responseBody.setStockOutCheckList(stockOutChecks);
        } else if (StockOutCheckRequest.ACTION_GET_WLTCHECK_LIST.equals(actionType)) {
            List<NBOwnerReferenceList> wltStockOutChecks = gcService.getWltStockOutCheckList();
            responseBody.setWltStockOutCheckList(wltStockOutChecks);
        } else if (StockOutCheckRequest.ACTION_JUDGE.equals(actionType)) {
            List<MaterialLot> materialLots = requestBody.getMaterialLots();
            materialLots = materialLots.stream().map(materialLot -> mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true)).collect(Collectors.toList());
            gcService.stockOutCheck(materialLots, requestBody.getCheckList());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
