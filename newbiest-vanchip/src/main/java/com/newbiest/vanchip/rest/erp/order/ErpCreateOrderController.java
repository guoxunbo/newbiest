package com.newbiest.vanchip.rest.erp.order;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.MaterialLot;
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
@RequestMapping("/erp")
@Slf4j
@Api(value="/erp", tags="VanChip客制化ERP接口", description = "ERP创建盘点单据接口")
public class ErpCreateOrderController extends AbstractRestController {

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "ERP创建单据")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ErpCreateOrderRequest")
    @RequestMapping(value = "/createOrder", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ErpCreateOrderResponse execute(@RequestBody ErpCreateOrderRequest request) throws Exception {
        ErpCreateOrderResponse response = new ErpCreateOrderResponse();
        ErpCreateOrderResponseBody responseBody = new ErpCreateOrderResponseBody();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        String actionType = request.getBody().getActionType();
        Document document = request.getBody().getDocument();
        List<MaterialLot> materialLotList = request.getBody().getMaterialLotList();

        if (ErpCreateOrderRequest.ACTION_TYPE_CREATE_CHECK_ORDER.equals(actionType)){
            documentService.createCheckOrder(document, materialLotList);
        }else if (ErpCreateOrderRequest.ACTION_TYPE_CREATE_SCRAP_ORDER.equals(actionType)){
            documentService.createScrapOrder(document, materialLotList);
        }else if (ErpCreateOrderRequest.ACTION_TYPE_DELETE_SCRAP_ORDER.equals(actionType)){
            documentService.deleteScrapOrder(document);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }
}
