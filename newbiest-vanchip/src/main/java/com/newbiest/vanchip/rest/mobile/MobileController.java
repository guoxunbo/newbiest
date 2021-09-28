package com.newbiest.vanchip.rest.mobile;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
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

/**
 * 手持端的操作
 */
@RestController()
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc")
@Deprecated
public class MobileController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "手持端的操作", notes = "接收，入库，出库，包装，出货，盘点")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MobileRequest")
    @RequestMapping(value = "/mobileManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MobileResponse execute(@RequestBody MobileRequest request) throws Exception {
        MobileResponse response = new MobileResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MobileResponseBody responseBody = new MobileResponseBody();
        MobileRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if (MobileRequest.ACTION_STOCK_IN.equals(actionType)){
            List<MaterialLot> materialLots = Lists.newArrayList();
            MaterialLot materialLot= vanChipService.stockInMLotMobile(requestBody.getMaterialLotAction());
            materialLots.add(materialLot);
            responseBody.setMaterialLots(materialLots);
        } else if (MobileRequest.ACTION_STOCK_IN_FINISH_GOOD.equals(actionType)) {

            vanChipService.stockInMLotMobile(requestBody.getMaterialLotAction());
        } else if (MobileRequest.ACTION_VALIDATE_STOCK_IN_BY_ORDER.equals(actionType)){
            vanChipService.validataStockInByOrder(requestBody.getDocumentId(), requestBody.getMaterialLotActions());

        } else if (MobileRequest.ACTION_STOCK_IN_BY_ORDER.equals(actionType)){

            vanChipService.stockInMLotByOrderMobile(requestBody.getDocumentId(), requestBody.getMaterialLotActions());
        }else if (MobileRequest.ACTION_STOCK_OUT.equals(actionType)){
            List<MaterialLot> materialLots = Lists.newArrayList();
            MaterialLot materialLot = vanChipService.stockOutMLotMobile(requestBody.getMaterialLotAction());
            materialLots.add(materialLot);
            responseBody.setMaterialLots(materialLots);
        }else if (MobileRequest.ACTION_STOCK_OUT_BY_ORDER.equals(actionType)){
            vanChipService.stockOutMLotByOrder(requestBody.getDocumentId(), requestBody.getMaterialLotActions());

        }else if (MobileRequest.ACTION_QUERY_PACKAGE_MLOT.equals(actionType)){
            List<MaterialLot> materialLots = Lists.newArrayList();
            MaterialLot materialLot = vanChipService.queryPackageMLotMobile(requestBody.getMaterialLotAction());
            materialLots.add(materialLot);
            responseBody.setMaterialLots(materialLots);
        }else if (MobileRequest.ACTION_PACKAGE_MLOT.equals(actionType)){
            List<MaterialLot> materialLots = Lists.newArrayList();
            MaterialLot materialLot = vanChipService.packageMaterialLots(requestBody.getMaterialLotActions(), requestBody.getPackageType());
            materialLots.add(materialLot);
            responseBody.setMaterialLots(materialLots);
        }else if (MobileRequest.ACTION_QUERY_SHIP_MLOT_BY_DOC.equals(actionType)){

        }else if (MobileRequest.ACTION_CHECK_MLOT_INVENTORY.equals(actionType)){
            //vanChipService.checkMlotInventoryMobile(requestBody.getMaterialLotAction());
        }else if (MobileRequest.ACTION_TRANSFER_INVENTORY.equals(actionType)){
            vanChipService.transferInvMobile(requestBody.getMaterialLotAction());
        }else if (MobileRequest.ACTION_VAILADATE_FROM_WAREHOUSE.equals(actionType)){
            vanChipService.valiadateFromWarehouse(requestBody.getMaterialLotAction());
        }else if (MobileRequest.ACTION_VAILADATE_TARGET_WAREHOUSE.equals(actionType)){
            vanChipService.valiadateTargetWarehouse(requestBody.getMaterialLotAction());
        }else if (MobileRequest.ACTION_TRANSFER_INVENTORY_MLOTS.equals(actionType)){
            vanChipService.transferInvMLots(requestBody.getMaterialLotActions());
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
