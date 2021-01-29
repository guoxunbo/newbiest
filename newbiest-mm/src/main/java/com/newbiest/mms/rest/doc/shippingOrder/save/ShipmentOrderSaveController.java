package com.newbiest.mms.rest.doc.shippingOrder.save;

import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.service.DocumentService;
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

@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class ShipmentOrderSaveController {

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "发货通知单保存")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ShipmentOrderSaveRequest")
    @RequestMapping(value = "/shipmentOrderSave", method = RequestMethod.POST)
    public ShipmentOrderSaveResponse excute(@RequestBody ShipmentOrderSaveRequest request){
        ShipmentOrderSaveRequestBody requestBody = request.getBody();
        ShipmentOrderSaveResponse response = new ShipmentOrderSaveResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ShipmentOrderSaveResponseBody responseBody = new ShipmentOrderSaveResponseBody();

        List<DocumentLine> documentLine = documentService.shipmentOrderSave(requestBody.getDocumentLineList());
        responseBody.setDocumentLineList(documentLine);
        response.setBody(responseBody);
        return response;
    }
}

