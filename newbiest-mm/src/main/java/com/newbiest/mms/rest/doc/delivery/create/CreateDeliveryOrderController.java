package com.newbiest.mms.rest.doc.delivery.create;

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
public class CreateDeliveryOrderController {

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "发货单保存")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "CreateDeliveryOrderRequest")
    @RequestMapping(value = "/createDeliveryOrder", method = RequestMethod.POST)
    public CreateDeliveryOrderResponse excute(@RequestBody CreateDeliveryOrderRequest request){
        CreateDeliveryOrderRequestBody requestBody = request.getBody();
        CreateDeliveryOrderResponse response = new CreateDeliveryOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        CreateDeliveryOrderResponseBody responseBody = new CreateDeliveryOrderResponseBody();

        documentService.createDeliveryOrder(requestBody.getDocumentId(), true, requestBody.getDocumentLineList());
        response.setBody(responseBody);
        return response;
    }
}

