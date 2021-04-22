package com.newbiest.vanchip.rest.doc.issue.create.laboratory;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.Document;
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

@RestController("VCCreateIssueOrderController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc")
public class CreateIssueOrderController extends AbstractRestController {

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "创建发料单")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "CreateIssueOrderRequest")
    @RequestMapping(value = "/createIssueOrder", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public CreateIssueOrderResponse execute(@RequestBody CreateIssueOrderRequest request) throws Exception {
        CreateIssueOrderResponse response = new CreateIssueOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        CreateIssueOrderResponseBody responseBody = new CreateIssueOrderResponseBody();
        CreateIssueOrderRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if(CreateIssueOrderRequest.ACTION_TYPE_CREATE_ISSUE_LABMLOT_ORDER.equals(actionType)){
            Document document = documentService.createIssueLabMLotOrder(requestBody.getDocumentId(), true, requestBody.getMaterials());

            responseBody.setDocument(document);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
