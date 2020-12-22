package com.newbiest.gc.rest.receive.rma;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.rest.receive.ft.FTMLotManagerRequest;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.dto.MaterialLotAction;
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
import java.util.Map;

/**
 * Created by guozhangLuo on 2020-12-14
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class RMAMLotManagerController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "RMA来料接收", notes = "接收")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RMAMLotManagerRequest")
    @RequestMapping(value = "/RmaMaterialLotManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RMAMLotManagerResponse execute(@RequestBody RMAMLotManagerRequest request) throws Exception {
        RMAMLotManagerResponse response = new RMAMLotManagerResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        RMAMLotManagerResponseBody responseBody = new RMAMLotManagerResponseBody();
        RMAMLotManagerRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        String printLabel = requestBody.getPrintLabel();
        List<Map<String, String>> parameterMapList = Lists.newArrayList();

        if (RMAMLotManagerRequest.ACTION_TYPE_RECEIVE.equals(actionType)) {
            List<MaterialLotAction> materialLotActions = requestBody.getMaterialLotActions();
            List<MaterialLot> materialLots = gcService.receiveRmaMLot(materialLotActions);
            if(!StringUtils.isNullOrEmpty(printLabel)){
                parameterMapList = gcService.getRmaLabelPrintParameter(materialLots);
            }
            responseBody.setParameterMapList(parameterMapList);
        } else if(RMAMLotManagerRequest.ACTION_TYPE_PRINT.equals(actionType)) {
            parameterMapList = gcService.getRmaLabelPrintParameter(requestBody.getMaterialLots());
            responseBody.setParameterMapList(parameterMapList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + request.getBody().getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
