package com.newbiest.mms.rest.doc.delivery.create;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
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

@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class CreateDeliveryOrderController {

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "创建发货单",notes = "创建，审核")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "CreateDeliveryOrderRequest")
    @RequestMapping(value = "/createDeliveryOrder", method = RequestMethod.POST)
    public CreateDeliveryOrderResponse excute(@RequestBody CreateDeliveryOrderRequest request){
        CreateDeliveryOrderRequestBody requestBody = request.getBody();
        CreateDeliveryOrderResponse response = new CreateDeliveryOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        CreateDeliveryOrderResponseBody responseBody = new CreateDeliveryOrderResponseBody();

        String actionType = requestBody.getActionType();
        if (CreateDeliveryOrderRequest.ACTION_TYPE_CREATE.equals(actionType)){

            documentService.createDeliveryOrder(requestBody.getDocumentId(), false, requestBody.getDocumentLineList());
        } else if (CreateDeliveryOrderRequest.ACTION_TYPE_APPROVE.equals(actionType)){

            documentService.approveDocument(requestBody.getDocumentId());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}

