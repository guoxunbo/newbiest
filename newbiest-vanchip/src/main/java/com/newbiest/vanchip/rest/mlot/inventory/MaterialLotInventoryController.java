package com.newbiest.vanchip.rest.mlot.inventory;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLotInventory;
import com.newbiest.mms.service.MmsService;
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
@RequestMapping("/vc")
@Slf4j
@Api(value="vc", tags="VanChip客制化")
public class MaterialLotInventoryController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "对物料库存做操作,根据单据获得库存物料,批量领料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotInvRequest")
    @RequestMapping(value = "/inventoryManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotInventoryResponse execute(@RequestBody MaterialLotInventoryRequest request) throws Exception {
        MaterialLotInventoryResponse response = new MaterialLotInventoryResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotInventoryResponseBody responseBody = new MaterialLotInventoryResponseBody();

        MaterialLotInventoryRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (MaterialLotInventoryRequest.ACTION_GET_MATERIAL_LOT_BY_DOC.equals(actionType)){
            List<MaterialLotInventory> materialLotInventorys = vanChipService.getMLotInventoryByDoc(requestBody.getDocument());
            responseBody.setMaterialLotInventorys(materialLotInventorys);
        }else if (MaterialLotInventoryRequest.ACTION_GET_MATERIAL_LOT_BY_DOC_ID.equals(actionType)){
            List<MaterialLotInventory> materialLotInventorys = vanChipService.getMLotInventoryByDocId(requestBody.getDocumentId());
            responseBody.setMaterialLotInventorys(materialLotInventorys);
        }else if (MaterialLotInventoryRequest.ACTION_PICK.equals(actionType)){
            vanChipService.picks(requestBody.getMaterialLotActions());

        }else{
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }


        response.setBody(responseBody);
        return response;
    }

}
