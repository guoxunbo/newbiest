package com.newbiest.vanchip.rest.doc.issue.finishGood;


import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
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

@RestController()
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class IssueFinishGoodByDocController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "issueFinishGood", notes = "成品发料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IssueFinishGoodByDocRequest")
    @RequestMapping(value = "/issueFinishGoodByDoc", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IssueFinishGoodByDocResponse execute(@RequestBody IssueFinishGoodByDocRequest request) throws Exception {
        IssueFinishGoodByDocResponse response = new IssueFinishGoodByDocResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        IssueFinishGoodByDocResponseBody responseBody = new IssueFinishGoodByDocResponseBody();
        IssueFinishGoodByDocRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (IssueFinishGoodByDocRequest.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)){
            List<MaterialLot> materialLots = documentService.getReservedMLotByDocId(requestBody.getDocumentId());
            responseBody.setMaterialLots(materialLots);
        }else if (IssueFinishGoodByDocRequest.ACTION_TYPE_ISSUE_FINISH_GOOD.equals(actionType)){
            vanChipService.issueFinishGoodByDoc(requestBody.getDocumentId(), requestBody.getMaterialLotIdList());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }
}
