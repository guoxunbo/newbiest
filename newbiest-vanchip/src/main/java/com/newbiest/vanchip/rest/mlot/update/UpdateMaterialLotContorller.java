package com.newbiest.vanchip.rest.mlot.update;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
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
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip", description = "Vanchip客制化接口")

public class UpdateMaterialLotContorller {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "修改批次信息")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotUpdateRequest")
    @RequestMapping(value = "/updateMLot", method = RequestMethod.POST)
    public UpdateMaterialLotResponse excute(@RequestBody UpdateMaterialLotRequest request)throws Exception {
        UpdateMaterialLotRequestBody requestBody = request.getBody();

        UpdateMaterialLotResponse response = new UpdateMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        UpdateMaterialLotResponseBody responseBody = new UpdateMaterialLotResponseBody();

        String actionType = requestBody.getActionType();
        if (UpdateMaterialLotRequest.ACTION_TYPE_UPDATE_SO.equals(actionType)) {
            vanChipService.updateERPSo(requestBody.getMaterialLotList());
        } else if (UpdateMaterialLotRequest.ACTION_TYPE_UPDATE_ICL_DATE.equals(actionType)){
            vanChipService.updateProductionDate(requestBody.getMaterialLotId(), requestBody.getIclDateValue());
        } else if (UpdateMaterialLotRequest.ACTION_TYPE_UPDATE_RMA_NO.equals(actionType)){

            List<MaterialLot> materialLotList = vanChipService.updateRmaNo(requestBody.getMaterialLotList(), requestBody.getMaterialLotAction());
            responseBody.setMaterialLotList(materialLotList);
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
