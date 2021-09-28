package com.newbiest.vanchip.rest.doc.query;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.DocumentService;
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

import java.util.List;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class QueryOrderController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "单据查询")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "CreateOrderRequest")
    @RequestMapping(value = "/queryOrderManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public QueryOrderResponse execute(@RequestBody QueryOrderRequest request) throws Exception {
        QueryOrderResponse response = new QueryOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        QueryOrderResponseBody responseBody = new QueryOrderResponseBody();
        QueryOrderRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        String documentId = requestBody.getDocumentId();
        String materialLotId = requestBody.getMaterialLotId();
        String documentCategory = requestBody.getDocumentCategory();

        if (QueryOrderRequest.ACTION_QUERY_MLOT_BY_ORDER.equals(actionType)){
            List<MaterialLot> materialLots =  documentService.getMLotByDocumentId(documentId);
            responseBody.setMaterialLotList(materialLots);
        }else if (QueryOrderRequest.ACTION_QUERY_ORDER_BY_MLOT_ID.equals(actionType)){
            Document document = documentService.getDocumentByMLotIdAndDocumentCategory(materialLotId, documentCategory);
            List<MaterialLot> materialLots =  documentService.getMLotByDocumentId(document.getName());
            responseBody.setDocument(document);
            responseBody.setMaterialLotList(materialLots);
        }else if (QueryOrderRequest.ACTION_DELETE_DOCUMENT.equals(actionType)){

            documentService.deleteDocument(documentId);
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }

        response.setBody(responseBody);
        return response;
    }

}
