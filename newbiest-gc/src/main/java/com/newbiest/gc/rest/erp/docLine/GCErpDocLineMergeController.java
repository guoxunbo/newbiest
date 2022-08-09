package com.newbiest.gc.rest.erp.docLine;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.model.MLotDocRuleContext;
import com.newbiest.gc.service.GcService;
import com.newbiest.msg.Request;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gc")
@Slf4j
public class GCErpDocLineMergeController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "单据合并", notes = "erpDocLineMergeManger")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GCErpDocLineMergeRequest")
    @RequestMapping(value = "/erpDocLineMerge", method = RequestMethod.POST)
    public GCErpDocLineMergeResponse excute(@RequestBody GCErpDocLineMergeRequest request)throws Exception {
        GCErpDocLineMergeRequestBody requestBody = request.getBody();
        GCErpDocLineMergeResponse response = new GCErpDocLineMergeResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GCErpDocLineMergeResponseBody responseBody = new GCErpDocLineMergeResponseBody();

        String actionType = requestBody.getActionType();
        if(GCErpDocLineMergeRequest.ACTION_TYPE_MERGE_DOC.equals(actionType)){
            gcService.valaidateAndMergeErpDocLine(requestBody.getDocumentLines(), MLotDocRuleContext.MERGE_DOC_VALIDATE_RULE_ID);
        } else if(GCErpDocLineMergeRequest.ACTION_TYPE_HN_WAREHOUSE_MERGE_DOC.equals(actionType)){
            gcService.valaidateAndMergeErpDocLine(requestBody.getDocumentLines(), MLotDocRuleContext.HN_WAREHOUSE_MERGE_DOC_VALIDATE_RULE_ID);
        } else if(GCErpDocLineMergeRequest.ACTION_TYPE_BS_WAREHOUSE_MERGE_DOC.equals(actionType)){
            gcService.valaidateAndMergeErpDocLine(requestBody.getDocumentLines(), MLotDocRuleContext.BS_WAREHOUSE_MERGE_DOC_VALIDATE_RULE_ID);
        } else if(GCErpDocLineMergeRequest.ACTION_TYPE_CANCEL_MERGE_DOC.equals(actionType)){
            gcService.cancelErpMergeDocOrder(requestBody.getDocumentLines());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
