package com.newbiest.mms.rest.doc.issue.mlot;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
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
public class IssueMLotByDocController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @Autowired
    DocumentService documentService;
    
    @ApiOperation(value = "发料,指定物料批次")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IssueMLotByDocRequest")
    @RequestMapping(value = "/issueMLotByDoc", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IssueMLotByDocResponse execute(@RequestBody IssueMLotByDocRequest request) throws Exception {
        IssueMLotByDocResponse response = new IssueMLotByDocResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IssueMLotByDocResponseBody responseBody = new IssueMLotByDocResponseBody();
        IssueMLotByDocRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if(IssueMLotByDocRequest.ACTION_TYPE_ISSUE_MLOT_BY_ORDER.equals(actionType)){
            documentService.issueMaterialLotByDoc(requestBody.getDocumentId(), requestBody.getMaterialLotIdList());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }



}
