package com.newbiest.mms.rest.materiallot;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.RawMaterial;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PrintService;
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
public class MaterialLotController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @Autowired
    PrintService printService;

    @ApiOperation(value = "对物料批做操作", notes = "接收。消耗等")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotRequest")
    @RequestMapping(value = "/materialLotManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotResponse execute(@RequestBody MaterialLotRequest request) throws Exception {
        MaterialLotResponse response = new MaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotResponseBody responseBody = new MaterialLotResponseBody();

        MaterialLotRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        MaterialLot materialLot = requestBody.getMaterialLot();
        MaterialLotAction materialLotAction = requestBody.getMaterialLotAction();

        if (MaterialLotRequest.ACTION_RECEIVE_2_WAREHOUSE.equals(actionType)) {
            RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialLot.getMaterialName());
            if (rawMaterial == null) {
                throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, materialLot.getMaterialName());
            }
            materialLot = mmsService.receiveMLot2Warehouse(rawMaterial, materialLot.getMaterialLotId(), materialLotAction);
        } else if (MaterialLotRequest.ACTION_CONSUME.equals(actionType)) {
            materialLot = validationMaterialLot(materialLot);
            materialLot = mmsService.consumeMLot(materialLot, materialLotAction);
        } else if (MaterialLotRequest.ACTION_PRINT_LABEL.equals(actionType)) {
            materialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
            log.debug("transactionIp" + ThreadLocalContext.getTransactionIp());
            printService.printMLot(materialLot);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setMaterialLot(materialLot);
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
