package com.newbiest.vanchip.rest.incoming.mlot.receive;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.vanchip.service.VanChipService;
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
@RequestMapping("/vc")
@Slf4j
public class IncomingMaterialImportReceiveController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "来料接收", notes = "receive")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IncomingMaterialImportReceiveRequest")
    @RequestMapping(value = "/IncomingMaterialImportReceive", method = RequestMethod.POST)
    public IncomingMaterialImportReceiveResponse excute(@RequestBody IncomingMaterialImportReceiveRequest request)throws Exception {
        IncomingMaterialImportReceiveRequestBody requestBody = request.getBody();
        IncomingMaterialImportReceiveResponse response = new IncomingMaterialImportReceiveResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IncomingMaterialImportReceiveResponseBody responseBody = new IncomingMaterialImportReceiveResponseBody();

        String actionType = requestBody.getActionType();
        String incomingDocId =requestBody.getDocId();
        if(IncomingMaterialImportReceiveRequest.ACTION_TYPE_RECEIVE.equals(actionType)){
            List<MaterialLot> materialLotList = requestBody.getMaterialLots();
            //接收业务
            documentService.receiveIncomingLot(incomingDocId, materialLotList);
        }else if (IncomingMaterialImportReceiveRequest.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)){
            //获得根据单据号获得materialLot
            List<MaterialLot> materialLots =  vanChipService.getMaterialLotByIncomingDocId(incomingDocId);
            responseBody.setMaterialLotList(materialLots);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }
}