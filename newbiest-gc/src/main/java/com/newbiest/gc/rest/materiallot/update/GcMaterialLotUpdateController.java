package com.newbiest.gc.rest.materiallot.update;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.Request;
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
 * GlaxyCore 对物料批次的批量操作客制化
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcMaterialLotUpdateController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "对物料批做操作", notes = "修改入库备注、保税属性、扣留、释放")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcMaterialLotUpdateRequest")
    @RequestMapping(value = "/updateMaterialLot", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcMaterialLotUpdateResponse execute(@RequestBody GcMaterialLotUpdateRequest request) throws Exception {
        GcMaterialLotUpdateResponse response = new GcMaterialLotUpdateResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcMaterialLotUpdateResponseBody responseBody = new GcMaterialLotUpdateResponseBody();

        GcMaterialLotUpdateRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<MaterialLot> materialLotList = requestBody.getMaterialLotList();

        if (GcMaterialLotUpdateRequest.ACTION_UPDATE_TREASURY_NOTE.equals(actionType)) {
            gcService.updateMaterialLotTreasuryNote(materialLotList, requestBody.getTreasuryeNote());
        } else if(GcMaterialLotUpdateRequest.ACTION_QUERY.equals(actionType)){
            String materialLotId = requestBody.getMaterialLotId();
            MaterialLot materialLot = gcService.getWaitWeightMaterialLot(materialLotId);
            responseBody.setMaterialLot(materialLot);
        } else if(GcMaterialLotUpdateRequest.ACTION_UPDATE_LOCATION.equals(actionType)){
            gcService.updateMaterialLotLocation(materialLotList, requestBody.getLocation());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
