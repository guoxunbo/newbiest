package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialDelete;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.rest.boxQRCode.print.paamater.GcGetBoxQRCodePrintParaRequest;
import com.newbiest.gc.service.GcService;
import com.newbiest.msg.Request;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gc")
@Slf4j
public class IncomingMaterialDeleteController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "来料删除", notes = "delete")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IncomingMaterialDeleteRequest")
    @RequestMapping(value = "/IncomingMaterialDelete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IncomingMaterialDeleteResponse excute(@RequestBody IncomingMaterialDeleteRequest request) throws Exception {
        IncomingMaterialDeleteResponse response = new IncomingMaterialDeleteResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IncomingMaterialDeleteResponseBody responseBody = new IncomingMaterialDeleteResponseBody();
        IncomingMaterialDeleteRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();

        if(IncomingMaterialDeleteRequest.ACTION_DELETE_INCOMINGMLOT.equals(actionType)){
            gcService.deleteIncomingMaterialLot(requestBody.getMaterialLotUnitList(), requestBody.getDeleteNote());
        } else if(IncomingMaterialDeleteRequest.ACTION_DELETE_COGDETIAL.equals(actionType)){
            gcService.deleteCogDetail(requestBody.getLcdCogDetialList(), requestBody.getDeleteNote());
        } else if(IncomingMaterialDeleteRequest.ACTION_DELETE_COGECRETIVE.equals(actionType)){
            gcService.deleteCogEcretive(requestBody.getLcdCogEcretiveList(), requestBody.getDeleteNote());
        } else{
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
