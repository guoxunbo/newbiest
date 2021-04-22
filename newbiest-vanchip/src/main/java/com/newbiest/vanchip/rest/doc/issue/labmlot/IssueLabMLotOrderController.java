package com.newbiest.vanchip.rest.doc.issue.labmlot;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
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
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc")
public class IssueLabMLotOrderController extends AbstractRestController {

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "实验室发料管理")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IssueLabMLotOrderRequest")
    @RequestMapping(value = "/issueLabMLotManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IssueLabMLotOrderResponse execute(@RequestBody IssueLabMLotOrderRequest request) throws Exception {
        IssueLabMLotOrderResponse response = new IssueLabMLotOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IssueLabMLotOrderResponseBody responseBody = new IssueLabMLotOrderResponseBody();
        IssueLabMLotOrderRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if(IssueLabMLotOrderRequest.ACTION_TYPE_RECOMMEND_ISSUE_ORDER.equals(actionType)){
            List<MaterialLot> materialLots = documentService.recommendIssueLabMLot(requestBody.getDocumentId());
            responseBody.setMaterialLots(materialLots);
        } else if (IssueLabMLotOrderRequest.ACTION_TYPE_ISSUE_LAB_MLOT_ORDER.equals(actionType)) {

            documentService.issueLabMLot(requestBody.getDocumentId(), requestBody.getMaterialLotIds());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
