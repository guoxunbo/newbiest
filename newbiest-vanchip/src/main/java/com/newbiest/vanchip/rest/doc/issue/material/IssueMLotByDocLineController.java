package com.newbiest.vanchip.rest.doc.issue.material;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.DocumentLine;
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
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class IssueMLotByDocLineController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "发料 根据docLine进行发料， 手动选择物料批次")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IssueMLotByDocLineRequest")
    @RequestMapping(value = "/issueMLotByDocLine", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IssueMLotByDocLineResponse execute(@RequestBody IssueMLotByDocLineRequest request) throws Exception {
        IssueMLotByDocLineResponse response = new IssueMLotByDocLineResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IssueMLotByDocLineResponseBody responseBody = new IssueMLotByDocLineResponseBody();
        IssueMLotByDocLineRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        DocumentLine documentLine = requestBody.getDocumentLine();
        List<String>  materialLotIdList = requestBody.getMaterialLotIdList();

        if (IssueMLotByDocLineRequest.ACTION_TYPE_ISSUE.equals(actionType)) {

            vanChipService.issueMLotByDocLine(requestBody.getDocumentLine(), materialLotIdList);
        } else if (IssueMLotByDocLineRequest.ACTION_TYPE_VALIDATION.equals(actionType)){


        }else if (IssueMLotByDocLineRequest.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)){
            List<MaterialLot> materialLotList = documentService.getMLotByDocLine(documentLine);
            responseBody.setMaterialLotList(materialLotList);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }



}
