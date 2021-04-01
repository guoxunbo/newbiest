package com.newbiest.vanchip.rest.doc.finishGood.create;

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
@Api(value="/vc", tags="MaterialManagerSystem", description = "成品相关")
public class CreateFinishGoodOrderController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "对完成品做操作", notes = "mes调用接口将成品信息传入wms")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "FinishGoodReceiveRequest")
    @RequestMapping(value = "/createFinishGoodOrder", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public CreateFinishGoodOrderResponse execute(@RequestBody CreateFinishGoodOrderRequest request) throws Exception {
        CreateFinishGoodOrderResponse response = new CreateFinishGoodOrderResponse();
        CreateFinishGoodOrderResponseBody responseBody = new CreateFinishGoodOrderResponseBody();
        CreateFinishGoodOrderRequestBody requestBody = request.getBody();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        vanChipService.createFinishGoodOrder(requestBody.getDocumentId(), true, requestBody.getMaterialLots());
        response.setBody(responseBody);
        return response;
    }
}
