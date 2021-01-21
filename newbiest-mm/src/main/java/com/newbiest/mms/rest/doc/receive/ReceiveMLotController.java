package com.newbiest.mms.rest.doc.receive;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.RawMaterial;
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

import java.util.List;

@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class ReceiveMLotController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "根据单据接收物料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ReceiveMLotRequest")
    @RequestMapping(value = "/receiveMLotByDoc", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ReceiveMLotResponse execute(@RequestBody ReceiveMLotRequest request) throws Exception {
        ReceiveMLotResponse response = new ReceiveMLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ReceiveMLotResponseBody responseBody = new ReceiveMLotResponseBody();

        ReceiveMLotRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if(ReceiveMLotRequest.ACTION_TYPE_RECEIVE.equals(actionType)){
            List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
            documentService.receiveIncomingLot(requestBody.getDocumentId(), materialLotList);
        } else if (ReceiveMLotRequest.ACTION_TYPE_GET_MATERIAL_LOT.equals(actionType)){
            List<MaterialLot> materialLots = mmsService.getMLotByIncomingDocId(requestBody.getDocumentId());
            responseBody.setMaterialLotList(materialLots);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

    private MaterialLot validationMaterialLot(MaterialLot oldMaterialLot) {
        MaterialLot materialLot = mmsService.getMLotByMLotId(oldMaterialLot.getMaterialLotId());
        if (materialLot == null) {
            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, oldMaterialLot.getMaterialLotId());
        }
        validateEntity(oldMaterialLot);
        return materialLot;
    }

}
