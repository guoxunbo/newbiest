package com.newbiest.mms.rest.materiallot.inv;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotInventory;
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

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class MaterialLotInvController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "对物料库存做操作", notes = "出入库，转库, 盘点等")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotInvRequest")
    @RequestMapping(value = "/materialLotInvManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotInvResponse execute(@RequestBody MaterialLotInvRequest request) throws Exception {
        MaterialLotInvResponse response = new MaterialLotInvResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotInvResponseBody responseBody = new MaterialLotInvResponseBody();

        MaterialLotInvRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        MaterialLot materialLot = requestBody.getMaterialLot();
        MaterialLotAction materialLotAction = requestBody.getMaterialLotAction();

        MaterialLotInventory materialLotInventory = null;

        String materialLotId = materialLot.getMaterialLotId();
        materialLot = mmsService.getMLotByMLotId(materialLotId);
        if (materialLot == null) {
            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
        }

        if (MaterialLotInvRequest.ACTION_STOCK_OUT.equals(actionType)) {
            //mmsService.stockOut(materialLot, materialLotAction);
            mmsService.pick(materialLot, materialLotAction);
        } else if (MaterialLotInvRequest.ACTION_TRANSFER.equals(actionType)) {
            materialLotInventory = mmsService.transfer(materialLot, materialLotAction);
        } else if (MaterialLotInvRequest.ACTION_PICK.equals(actionType)) {
            mmsService.pick(materialLot, materialLotAction);
        } else if (MaterialLotInvRequest.ACTION_CHECK.equals(actionType)) {
            //materialLotInventory = mmsService.checkMaterialInventory(materialLot, materialLotAction);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setMaterialLotInventory(materialLotInventory);
        response.setBody(responseBody);
        return response;
    }

}
