package com.newbiest.mms.rest.doc.issue.material;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
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
public class IssueMaterialByOrderController extends AbstractRestController {

    @Autowired
    DocumentService documentService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "指定物料发料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IssueLabMLotOrderRequest")
    @RequestMapping(value = "/issueMaterialManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IssueMaterialByOrderResponse execute(@RequestBody IssueMaterialByOrderRequest request) throws Exception {
        IssueMaterialByOrderResponse response = new IssueMaterialByOrderResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IssueMaterialByOrderResponseBody responseBody = new IssueMaterialByOrderResponseBody();
        IssueMaterialByOrderRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if(IssueMaterialByOrderRequest.ACTION_TYPE_RECOMMEND_ISSUE_ORDER.equals(actionType)){
            List<MaterialLot> materialLots = documentService.recommendIssueByMaterialOrder(requestBody.getDocumentId());
            responseBody.setMaterialLots(materialLots);
        } else if (IssueMaterialByOrderRequest.ACTION_TYPE_ISSUE_MATERIAL_BY_ORDER.equals(actionType)) {

            documentService.issueByMaterial(requestBody.getDocumentId(), requestBody.getMaterialLotIds());
        }else if (IssueMaterialByOrderRequest.ACTION_TYPE_GET_MATERIAL_STOCK_QTY.equals(actionType)){

            List<Material> materials = mmsService.getMaterialStockQty(requestBody.getMaterials());
            responseBody.setMaterials(materials);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
