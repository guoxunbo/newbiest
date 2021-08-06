package com.newbiest.mms.rest.doc.back.mlot;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.impl.DocumentServiceImpl;
import com.newbiest.mms.state.model.MaterialStatus;
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

@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class ReturnMLotByDocController extends AbstractRestController {

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "退料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ReturnMLotByDocRequest")
    @RequestMapping(value = "/returnMLotByDoc", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ReturnMLotByDocResponse execute(@RequestBody ReturnMLotByDocRequest request) throws Exception {
        ReturnMLotByDocResponse response = new ReturnMLotByDocResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ReturnMLotByDocResponseBody responseBody = new ReturnMLotByDocResponseBody();
        ReturnMLotByDocRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if(ReturnMLotByDocRequest.ACTION_TYPE_RETURN_MLOT.equals(actionType)){
            documentService.returnMLotByDoc(requestBody.getDocumentId(), requestBody.getMaterialLotIdList(), DocumentServiceImpl.RETURN_WAREHOUSE);
        } else if (ReturnMLotByDocRequest.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)){
            List<MaterialLot> materialLotList = documentService.getReservedMLotByDocId(requestBody.getDocumentId());
            materialLotList = materialLotList.stream().filter(mLot-> MaterialStatus.STATUS_IN.equals(mLot.getStatus())
                    || MaterialStatus.STATUS_WAIT.equals(mLot.getStatus()) || MaterialStatus.STATUS_RETURN.equals(mLot.getStatus())).collect(Collectors.toList());
            responseBody.setMaterialLotList(materialLotList);
        } else if (ReturnMLotByDocRequest.ACTION_TYPE_RETURN_MATERIAL_LOT.equals(actionType)){
            documentService.returnMLotByDoc(requestBody.getDocumentId(), requestBody.getMaterialLotIdList(), DocumentServiceImpl.RETURN_SUPPLIER);
        } else if (ReturnMLotByDocRequest.ACTION_TYPE_RETURN_GOODS.equals(actionType)){
            documentService.returnLotOrder(requestBody.getDocumentId(), requestBody.getMaterialLotIdList());
        } else if (ReturnMLotByDocRequest.ACTION_TYPE_DEPT_RETURN_MLOT.equals(actionType)){
            documentService.deptReturnMLot(requestBody.getDocumentId(), requestBody.getMaterialLotIdList());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }



}
