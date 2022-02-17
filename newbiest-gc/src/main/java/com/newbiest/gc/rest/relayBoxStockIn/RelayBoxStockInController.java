package com.newbiest.gc.rest.relayBoxStockIn;


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

import java.util.List;

@RestController("GcRelayBoxStockInController")
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class RelayBoxStockInController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "RelayBoxStockIn", notes = "更换库位")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RelayBoxStockInRequest")
    @RequestMapping(value = "/relayBoxStockIn", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RelayBoxStockInResponse execute(@RequestBody RelayBoxStockInRequest request)  throws Exception {
        RelayBoxStockInResponse response = new RelayBoxStockInResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        RelayBoxStockInResponseBody responseBody = new RelayBoxStockInResponseBody();
        RelayBoxStockInRequestBody requestBody =  request.getBody();
        String actionType = requestBody.getActionType();

        if (RelayBoxStockInRequest.ACTION_QUERY_BOX.equals(actionType)) {
            MaterialLot materialLot = gcService.getWaitStockInStorageMaterialLotByLotIdOrMLotId(requestBody.getMaterialLotId(), requestBody.getTableRrn());
            responseBody.setMaterialLot(materialLot);
        } else if(RelayBoxStockInRequest.ACTION_QUERY_RELAYBOX.equals(actionType)){
            String relayBoxId = requestBody.getRelayBoxId();
            List<MaterialLot> materialLots = gcService.getWaitChangeStorageMaterialLotByRelayBoxId(relayBoxId);
            responseBody.setMaterialLots(materialLots);
        } else if (RelayBoxStockInRequest.ACTION_RELAYBOX_STOCK_IN.equals(actionType)) {
            gcService.transferStorage(requestBody.getRelayBoxStockInModels());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
