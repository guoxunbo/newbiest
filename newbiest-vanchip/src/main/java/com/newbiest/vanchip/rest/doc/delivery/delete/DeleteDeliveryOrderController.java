package com.newbiest.vanchip.rest.doc.delivery.delete;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.vanchip.service.VanChipService;
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
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class DeleteDeliveryOrderController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "删除发货通知单")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "DeleteDeliveryOrderRequest")
    @RequestMapping(value = "/deleteDeliveryOrder", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public DeleteDeliveryOrderResponse execute(@RequestBody DeleteDeliveryOrderRequest request) throws Exception {
        DeleteDeliveryOrderResponse response = new DeleteDeliveryOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        DeleteDeliveryOrderResponseBody responseBody = new DeleteDeliveryOrderResponseBody();
        DeleteDeliveryOrderRequestBody requestBody = request.getBody();

        String actionTyep = requestBody.getActionType();
        if (DeleteDeliveryOrderRequest.ACTION_TYPE_DELETE.equals(actionTyep)){
            DocumentLine documentLine = vanChipService.deleteDeliveryOrder(requestBody.getDeliveryLineId());
            responseBody.setDocumentLine(documentLine);
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionTyep);
        }


        response.setBody(responseBody);
        return response;
    }

}
