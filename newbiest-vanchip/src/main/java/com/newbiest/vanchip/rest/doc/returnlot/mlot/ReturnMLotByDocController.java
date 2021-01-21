package com.newbiest.vanchip.rest.doc.returnlot.mlot;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
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
public class ReturnMLotByDocController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

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

           vanChipService.returnMLotByDoc(requestBody.getDocumentId(),requestBody.getMaterialLotIdList());
        } else if (ReturnMLotByDocRequest.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)){

            List<MaterialLot> materialLotList = vanChipService.getMLotByDocId(requestBody.getDocumentId());
            responseBody.setMaterialLotList(materialLotList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }



}
