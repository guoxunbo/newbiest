package com.newbiest.mms.gc.rest.stockout.check;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.gc.model.StockOutCheck;
import com.newbiest.mms.gc.model.service.GcService;
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

/**
 * GlaxyCore 对物料批次的批量操作客制化
 * 中转箱，备货等等
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
        MaterialLot materialLot = requestBody.getMaterialLot();

        if (StockOutCheckRequest.ACTION_GET_CHECK_LIST.equals(actionType)) {
            List<StockOutCheck> stockOutChecks = gcService.getStockOutCheckList();
            responseBody.setStockOutCheckList(stockOutChecks);
        } else if (StockOutCheckRequest.ACTION_JUDGE.equals(actionType)) {
            materialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true);
            gcService.stockOutCheck(materialLot, requestBody.getCheckResult());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
