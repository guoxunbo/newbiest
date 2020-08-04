package com.newbiest.gc.rest.record.express;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.ExpressService;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.DeliveryOrder;
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

/**
 * Created by guoxunbo on 2019-08-21 13:15
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class RecordExpressController {

    @Autowired
    GcService gcService;

    @Autowired
    ExpressService expressService;

    @ApiOperation(value = "RecordExpress", notes = "记录快递单号")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RecordExpressRequest")
    @RequestMapping(value = "/recordExpress", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RecordExpressResponse execute(@RequestBody RecordExpressRequest request) throws Exception {
        RecordExpressResponse response = new RecordExpressResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        RecordExpressResponseBody responseBody = new RecordExpressResponseBody();
        RecordExpressRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        List<MaterialLot> materialLots = Lists.newArrayList();
        if (RecordExpressRequestBody.ACTION_TYPE_AUTO_ORDER.equals(actionType)) {
            materialLots = expressService.planOrder(requestBody.getMaterialLots(), requestBody.getServiceMode(), requestBody.getPayMode());
        } else if (RecordExpressRequestBody.ACTION_TYPE_MANUAL_ORDER.equals(actionType)) {
            materialLots = expressService.recordExpressNumber(requestBody.getMaterialLots(), requestBody.getExpressNumber(), MaterialLot.PLAN_ORDER_TYPE_MANUAL);
        } else if (RecordExpressRequestBody.ACTION_TYPE_CANCEL_ORDER.equals(actionType)) {
            expressService.cancelOrderByMaterialLots(requestBody.getMaterialLots());
        } else if (RecordExpressRequestBody.ACTION_TYPE_OLD_RECORD_ORDER.equals(actionType)) {
            List<DeliveryOrder> deliveryOrders = expressService.recordExpressNumber(requestBody.getDeliveryOrderList());
            responseBody.setDeliveryOrderList(deliveryOrders);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + request.getBody().getActionType());
        }
        responseBody.setMaterialLots(materialLots);
        response.setBody(responseBody);
        return response;
    }
}
