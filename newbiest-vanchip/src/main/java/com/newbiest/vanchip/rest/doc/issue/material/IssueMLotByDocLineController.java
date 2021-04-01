package com.newbiest.vanchip.rest.doc.issue.material;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
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

@RestController("VCIssueMLotByDocLineController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
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

        List<String> materialLotIdList = requestBody.getMaterialLotIdList();
        String actionType = requestBody.getActionType();
        if (IssueMLotByDocLineRequest.ACTION_TYPE_ISSUE.equals(actionType)){
            vanChipService.issueMLotByDocLine(requestBody.getDocumentLine(), materialLotIdList);
        }else if (IssueMLotByDocLineRequest.ACTION_TYPE_VALIDATION.equals(actionType)){
            List<MaterialLot> materialLotList = Lists.newArrayList();
            MaterialLot materialLot = vanChipService.validationDocLineAndMaterialLot(requestBody.getDocumentLine(), materialLotIdList);
            materialLotList.add(materialLot);
            responseBody.setMaterialLotList(materialLotList);
        }else if(IssueMLotByDocLineRequest.ACTION_TYPE_GET_MLOT.equals(actionType)){
            List<MaterialLot> materialLots = vanChipService.getMLotByFIFO(requestBody.getDocumentLine());
            responseBody.setMaterialLotList(materialLots);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }



}
