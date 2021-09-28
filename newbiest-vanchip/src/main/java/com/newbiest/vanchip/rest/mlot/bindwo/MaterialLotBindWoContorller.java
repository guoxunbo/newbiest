package com.newbiest.vanchip.rest.mlot.bindwo;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
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

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip", description = "Vanchip客制化接口")

public class MaterialLotBindWoContorller {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "物料批次绑定WO")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "UpdateMaterialLotRequest")
    @RequestMapping(value = "/mlotBindWo", method = RequestMethod.POST)
    public MaterialLotBindWoResponse excute(@RequestBody MaterialLotBindWoRequest request)throws Exception {
        MaterialLotBindWoRequestBody requestBody = request.getBody();

        MaterialLotBindWoResponse response = new MaterialLotBindWoResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotBindWoResponseBody responseBody = new MaterialLotBindWoResponseBody();

        String actionType = requestBody.getActionType();
        if (MaterialLotBindWoRequest.ACTION_TYPE_BIND.equals(actionType)) {
            vanChipService.bindMesOrder(requestBody.getMaterialLotIdList(), requestBody.getWorkOrderId());
        } else if (MaterialLotBindWoRequest.ACTION_TYPE_UNBIND.equals(actionType)) {
            vanChipService.unbindMesOrder(requestBody.getMaterialLotIdList());
        } else if (MaterialLotBindWoRequest.ACTION_TYPE_UNBIND_AND_BIND.equals(actionType)){
            vanChipService.unbindMesOrder(requestBody.getMaterialLotIdList());

            vanChipService.bindMesOrder(requestBody.getMaterialLotIdExtList(), requestBody.getWorkOrderId());

        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
