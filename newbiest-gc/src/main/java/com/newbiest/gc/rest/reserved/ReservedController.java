package com.newbiest.gc.rest.reserved;

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

/**
 * Created by guoxunbo on 2019-08-21 13:15
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class ReservedController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "Reserved", notes = "备货")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ReservedRequest")
    @RequestMapping(value = "/reserved", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ReservedResponse execute(@RequestBody ReservedRequest request) throws Exception {
        ReservedResponse response = new ReservedResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        ReservedResponseBody responseBody = new ReservedResponseBody();
        ReServedRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        if (ReServedRequestBody.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)) {
            List<MaterialLot> waitForReservedMaterialLots = gcService.getWaitForReservedMaterialLot(requestBody.getDocLineRrn(), requestBody.getTableRrn());
            responseBody.setMaterialLotList(waitForReservedMaterialLots);
        } else if (ReServedRequestBody.ACTION_TYPE_RESERVED.equals(actionType)) {
            gcService.reservedMaterialLot(requestBody.getDocLineRrn(), requestBody.getMaterialLotActions());
        } else if (ReServedRequestBody.ACTION_TYPE_UN_RESERVED.equals(actionType)) {
            gcService.unReservedMaterialLot(requestBody.getMaterialLotActions());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
