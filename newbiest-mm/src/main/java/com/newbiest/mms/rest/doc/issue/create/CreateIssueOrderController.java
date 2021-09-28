package com.newbiest.mms.rest.doc.issue.create;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
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
public class CreateIssueOrderController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

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

        String docId = StringUtils.EMPTY;
        String actionType = requestBody.getActionType();
        if(CreateIssueOrderRequest.ACTION_TYPE_CREATE_ISSUE_LOT_ORDER.equals(actionType)){
            docId = documentService.createIssueLotOrder(requestBody.getDocumentId(), true, requestBody.getMaterialLotIdList());
        }else if (CreateIssueOrderRequest.ACTION_TYPE_CREATE_ISSUE_MATERIAL_ORDER.equals(actionType)){
            docId = documentService.createIssueMaterialOrder(requestBody.getDocumentId(), true, requestBody.getMaterialLotIdList());
        }else if (CreateIssueOrderRequest.ACTION_TYPE_CREATE_ISSUE_FINISH_GOOD_ORDER.equals(actionType)){
            documentService.createIssueFinishGoodOrder(requestBody.getDocumentId(), true, requestBody.getMaterialLotIdList());
        }else if (CreateIssueOrderRequest.ACTION_TYPE_CREATE_ISSUE_ORDER_BY_MATERIAL.equals(actionType)){
            Document document = documentService.createIssueByMaterialOrder(requestBody.getDocumentId(), true, requestBody.getMaterials(), requestBody.getMaterialLotAction());
            responseBody.setDocument(document);
        }else if (CreateIssueOrderRequest.ACTION_TYPE_CREATE_ISSUE_ORDER_BY_MLOT.equals(actionType)){
            Document document = documentService.createIssueMaterialLotOrder(requestBody.getDocumentId(), true, requestBody.getMaterialLots(), requestBody.getMaterialLotAction());
            responseBody.setDocument(document);
        }else if (CreateIssueOrderRequest.ACTION_TYPE_CREATE_ISSUE_PARTS_ORDER.equals(actionType)){

            documentService.createIssuePartsOrder(requestBody.getDocumentId(), true, requestBody.getMaterialName(), requestBody.getQty(), requestBody.getCreator(), requestBody.getPartComments());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        responseBody.setDocumentId(docId);
        response.setBody(responseBody);
        return response;
    }

}
