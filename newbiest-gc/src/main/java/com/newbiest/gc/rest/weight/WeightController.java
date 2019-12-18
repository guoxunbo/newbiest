package com.newbiest.gc.rest.weight;


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

@RestController("GcWeightController")
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class WeightController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "Weight", notes = "称重")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "WeightRequest")
    @RequestMapping(value = "/weight", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public WeightResponse execute(@RequestBody WeightRequest request) throws Exception {
        WeightResponse response = new WeightResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        WeightResponseBody responseBody = new WeightResponseBody();
        WeightRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (WeightRequest.ACTION_QUERY.equals(actionType)) {
            String materialLotId = requestBody.getMaterialLotId();
            MaterialLot materialLot = gcService.getWaitWeightMaterialLot(materialLotId);
            responseBody.setMaterialLot(materialLot);
        } else if (WeightRequest.ACTION_WEIGHT.equals(actionType)) {
            gcService.materialLotWeight(requestBody.getWeightModels());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);

        return response;
    }
}
