package com.newbiest.vanchip.rest.mlot.inventory;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.Parts;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
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

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="vc", tags="VanChip客制化")
public class MaterialLotInventoryController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    DocumentService documentService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "对物料库存做操作,根据单据获得库存物料,批量领料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotInvRequest")
    @RequestMapping(value = "/inventoryManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotInventoryResponse execute(@RequestBody MaterialLotInventoryRequest request) throws Exception {
        MaterialLotInventoryResponse response = new MaterialLotInventoryResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotInventoryResponseBody responseBody = new MaterialLotInventoryResponseBody();

        MaterialLotInventoryRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<MaterialLotAction> materialLotActions = requestBody.getMaterialLotActions();

        if (MaterialLotInventoryRequest.ACTION_GET_STOCK_OUT_MLOT_BY_ORDER.equals(actionType)){
            List<MaterialLot> materialLots =  documentService.getMLotByDocumentId(requestBody.getDocumentId());
            materialLots = materialLots.stream().filter(materialLot -> MaterialStatus.STATUS_IN.equals(materialLot.getStatus())).collect(Collectors.toList());
            responseBody.setMaterialLots(materialLots);
        }else if (MaterialLotInventoryRequest.ACTION_PICKS.equals(actionType)){
            vanChipService.picks(materialLotActions);
        }else if (MaterialLotInventoryRequest.ACTION_CREATE_PARTS_WAREHOUSE.equals(actionType)){
            Parts parts = mmsService.getPartsByName(requestBody.getMaterialLot().getMaterialName(), true);
            mmsService.receiveMLot2Warehouse(parts, requestBody.getMaterialLot().getMaterialLotId(), materialLotActions.get(0));
        }else if (MaterialLotInventoryRequest.ACTION_STOCK_OUT_PARTS_MLOT_BY_ORDER.equals(actionType)){

            vanChipService.stockOutPartsByOrder(requestBody.getDocumentId(), materialLotActions.get(0));
        }else if (MaterialLotInventoryRequest.ACTION_STOCK_OUT_PARTS_MLOT.equals(actionType)){
            Parts parts = mmsService.getPartsByName(requestBody.getMaterialLot().getMaterialName(), true);

            vanChipService.stockOutParts(parts, materialLotActions.get(0), null);
        }else if (MaterialLotInventoryRequest.ACTION_RETURN_PARTS_WAREHOUSE.equals(actionType)){
            Parts parts = mmsService.getPartsByName(requestBody.getMaterialLot().getMaterialName(), true);

            vanChipService.returnMLotWarehouse(parts, requestBody.getMaterialLot().getMaterialLotId(), materialLotActions.get(0));
        }else{
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }


        response.setBody(responseBody);
        return response;
    }

}
