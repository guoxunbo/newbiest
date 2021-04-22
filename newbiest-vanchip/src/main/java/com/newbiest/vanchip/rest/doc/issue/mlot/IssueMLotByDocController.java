package com.newbiest.vanchip.rest.doc.issue.mlot;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.state.model.MaterialStatus;
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
import java.util.stream.Collectors;

@RestController("VCIssueMLotByDocController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class IssueMLotByDocController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "发料,指定物料批次")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IssueMLotByDocRequest")
    @RequestMapping(value = "/issueMLotByDoc", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IssueMLotByDocResponse execute(@RequestBody IssueMLotByDocRequest request) throws Exception {
        IssueMLotByDocResponse response = new IssueMLotByDocResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IssueMLotByDocResponseBody responseBody = new IssueMLotByDocResponseBody();
        IssueMLotByDocRequestBody requestBody = request.getBody();

        String actionTyep = requestBody.getActionType();
        if (IssueMLotByDocRequest.ACTION_TYPE_GET_WAIT_ISSUE_MLOT_BY_ORDER_ID.equals(actionTyep)){
            List<MaterialLot> materialLotList = vanChipService.getMLotByOrderId(requestBody.getDocumentId());

            if (CollectionUtils.isNotEmpty(materialLotList)){
                materialLotList = materialLotList.stream().filter(materialLot -> !MaterialStatus.STATUS_ISSUE.equals(materialLot.getStatus())).collect(Collectors.toList());
                responseBody.setMaterialLotList(materialLotList);
            }
        } else if (IssueMLotByDocRequest.ACTION_TYPE_ISSUE_MLOT_BY_DOC.equals(actionTyep)){
            vanChipService.issueMLotByOrder(requestBody.getDocumentId(), requestBody.getMaterialLotIdList());
        } else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionTyep);
        }


        response.setBody(responseBody);
        return response;
    }

}
