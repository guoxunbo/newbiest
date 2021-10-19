package com.newbiest.vanchip.rest.doc.check;

import com.newbiest.base.exception.ClientParameterException;
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

@RestController("CheckMLotController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class CheckMLotController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "盘点")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "CheckMLotRequest")
    @RequestMapping(value = "/checkMLotManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public CheckMLotResponse execute(@RequestBody CheckMLotRequest request) throws Exception {
        CheckMLotResponse response = new CheckMLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        CheckMLotResponseBody responseBody = new CheckMLotResponseBody();
        CheckMLotRequestBody requestBody = request.getBody();

        String actionTyep = requestBody.getActionType();
        if (CheckMLotRequest.ACTION_TYPE_GET_RESERVED_MLOT.equals(actionTyep)){
            List<MaterialLot> materialLotList = vanChipService.getReservedMLotByOrder(requestBody.getDocumentLine().getLineId());

            responseBody.setMaterialLotList(materialLotList);
        } else if (CheckMLotRequest.ACTION_TYPE_RECHECK_MLOT_BY_ORDER.equals(actionTyep)){
            vanChipService.recheckMLotInventorys(requestBody.getDocumentLine(), requestBody.getMaterialLotActionList());

        } else if (CheckMLotRequest.ACTION_TYPE_GET_RECHECK_MLOT.equals(actionTyep)){
            List<MaterialLot> materialLotList = vanChipService.getRecheckMLots(requestBody.getDocumentLine());

            responseBody.setMaterialLotList(materialLotList);
        }else if (CheckMLotRequest.ACTION_TYPE_CHECK_MLOT_BY_ORDER.equals(actionTyep)){

            vanChipService.checkMLotInventorys(requestBody.getDocumentLine(), requestBody.getMaterialLotActionList());
        } else if (CheckMLotRequest.ACTION_TYPE_SEND_MLOT_INV_BY_ERP.equals(actionTyep)){

            vanChipService.sendMLotInvByErp(requestBody.getDocumentLine(), requestBody.getMaterialLotActionList());
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionTyep);
        }


        response.setBody(responseBody);
        return response;
    }

}
