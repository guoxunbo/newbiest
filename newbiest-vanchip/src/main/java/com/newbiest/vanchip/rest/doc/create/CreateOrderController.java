package com.newbiest.vanchip.rest.doc.create;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.DocumentLine;
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

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class CreateOrderController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "手动创建单据")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "CreateOrderRequest")
    @RequestMapping(value = "/createOrder", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public CreateOrderResponse execute(@RequestBody CreateOrderRequest request) throws Exception {
        CreateOrderResponse response = new CreateOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        CreateOrderResponseBody responseBody = new CreateOrderResponseBody();
        CreateOrderRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        Document document = requestBody.getDocument();
        DocumentLine documentLine = requestBody.getDocumentLine();

        if (CreateOrderRequest.ACTION_CREATE_DOCUMENT.equals(actionType)){
            document = documentService.createDocument(document);
            responseBody.setDocument(document);
        }else if (CreateOrderRequest.ACTION_CREATE_DOCUMENT_LINE.equals(actionType)){
            documentLine = documentService.createDocLineByDocument(documentLine);
            responseBody.setDocumentLine(documentLine);
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
