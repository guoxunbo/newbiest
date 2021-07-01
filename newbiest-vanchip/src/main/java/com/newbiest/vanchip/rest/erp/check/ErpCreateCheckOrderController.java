package com.newbiest.vanchip.rest.erp.check;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.mms.model.Document;
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
@RequestMapping("/erp")
@Slf4j
@Api(value="/erp", tags="VanChip客制化ERP接口", description = "ERP创建盘点单据接口")
public class ErpCreateCheckOrderController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "ERP创建盘点单据")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ErpCreateCheckOrderRequest")
    @RequestMapping(value = "/createCheckOrder", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ErpCreateCheckOrderResponse execute(@RequestBody ErpCreateCheckOrderRequest request) throws Exception {
        ErpCreateCheckOrderResponse response = new ErpCreateCheckOrderResponse();
        ErpCreateCheckOrderResponseBody responseBody = new ErpCreateCheckOrderResponseBody();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        Document document = request.getBody().getDocument();
        String actionType = request.getBody().getActionType();

        if (Request.ACTION_CREATE.equals(actionType)){

        }else if (Request.ACTION_UPDATE.equals(actionType)){

        }else if (Request.ACTION_DELETE.equals(actionType)){

        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }
}
