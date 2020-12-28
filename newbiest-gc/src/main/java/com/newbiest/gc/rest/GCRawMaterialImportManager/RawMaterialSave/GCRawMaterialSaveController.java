package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.Request;
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
@RequestMapping("/gc")
@Slf4j
public class GCRawMaterialSaveController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "原材料处理", notes = "rawMaterialManger")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GCRawMaterialImportRequest")
    @RequestMapping(value = "/RawMaterialSave", method = RequestMethod.POST)
    public GCRawMaterialSaveResponse excute(@RequestBody GCRawMaterialSaveRequest request)throws Exception {
        GCRawMaterialSaveRequestBody requestBody = request.getBody();
        GCRawMaterialSaveResponse response = new GCRawMaterialSaveResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GCRawMaterialSaveResponseBody responseBody = new GCRawMaterialSaveResponseBody();

        String actionType = requestBody.getActionType();
        List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
        if(GCRawMaterialSaveRequest.ACTION_TYPE_CREATE.equals(actionType)){
            String  importCode =gcService.importRawMaterialLotList(materialLotList,requestBody.getImportType());
            responseBody.setImportCode(importCode);
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_RECEIVE.equals(actionType)){
            gcService.receiveRawMaterial(materialLotList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
