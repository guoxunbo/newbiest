package com.newbiest.vanchip.rest.pack;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.PrintService;
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

/**
 * 物料批次包装
 */
@RestController("VCPackMaterialLotController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc")
public class PackMaterialLotController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    PrintService printService;

    @ApiOperation(value = "物料批次包装", notes = "物料批次包装")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "PackMaterialLotRequest")
    @RequestMapping(value = "/packMaterialLots", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public PackMaterialLotResponse execute(@RequestBody PackMaterialLotRequest request) throws Exception {
        PackMaterialLotResponse response = new PackMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        PackMaterialLotResponseBody responseBody = new PackMaterialLotResponseBody();

        PackMaterialLotRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (PackMaterialLotRequest.ACTION_PACKAGE_MLOT.equals(actionType)){
            MaterialLot packagedMaterialLot = vanChipService.packageMaterialLots(requestBody.getMaterialLotActions(), requestBody.getPackageType());
            responseBody.setMaterialLot(packagedMaterialLot);
        }else if (PackMaterialLotRequest.ACTION_PRINT_PACKAGE_MLOT.equals(actionType)){
            vanChipService.printBoxMLot(requestBody.getMaterialLotId(), requestBody.getMaterialLotActions().get(0), requestBody.getValidationPrintFlag());
        }else if (PackMaterialLotRequest.ACTION_PRINT_RY_BOX_MLOT.equals(actionType)){

        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }

        response.setBody(responseBody);
        return response;
    }

}
