package com.newbiest.vanchip.rest.doc.returnlot.create;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
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
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class CreateReturnMLotOrderController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "创建退料单")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "CreateReturnMLotOrder")
    @RequestMapping(value = "/createReturnMLotOrder", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public CreateReturnMLotOrderResponse execute(@RequestBody CreateReturnMLotOrderRequest request) throws Exception {
        CreateReturnMLotOrderResponse response = new CreateReturnMLotOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        CreateReturnMLotOrderResponseBody responseBody = new CreateReturnMLotOrderResponseBody();
        CreateReturnMLotOrderRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if(CreateReturnMLotOrderRequest.ACTION_TYPE_CREATE_RETURN_MLOT_ORDER.equals(actionType)){

            vanChipService.createReturnMLotOrder(requestBody.getDocumentId(), true, requestBody.getMaterialLotIdAndQtyAndReasonMapList());
        }else if(CreateReturnMLotOrderRequest.ACTION_TYPE_CREATE_RETURN_MATERIAL_ORDER.equals(actionType)){

            vanChipService.createReturnMLotOrder(requestBody.getDocumentId(), true, requestBody.getMaterialLotIdAndQtyAndReasonMapList());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }
}
