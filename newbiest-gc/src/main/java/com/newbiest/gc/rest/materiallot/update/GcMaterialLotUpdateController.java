package com.newbiest.gc.rest.materiallot.update;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
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
        } else if(GcMaterialLotUpdateRequest.ACTION_UPDATE_LOCATION.equals(actionType)){
            gcService.updateMaterialLotLocation(materialLotList, requestBody.getLocation(), requestBody.getRemarks());
        } else if(GcMaterialLotUpdateRequest.ACTION_HOLD.equals(actionType)){
            gcService.materialLotHold(materialLotList, requestBody.getReason(), requestBody.getRemarks(), requestBody.getHoldType());
        } else if(GcMaterialLotUpdateRequest.ACTION_RELEASE.equals(actionType)){
            gcService.materialLotRelease(materialLotList, requestBody.getReason(), requestBody.getRemarks());
        } else if(GcMaterialLotUpdateRequest.ACTION_TYPE_QUERY_REFERENCE_LIST.equals(actionType)){
            List<NBOwnerReferenceList> referenceList = gcService.getReferenceListByName(requestBody.getReferenceName());
            responseBody.setReferenceList(referenceList);
        } else if(GcMaterialLotUpdateRequest.ACTION_TYPE_UPDATE_LOT_INFO.equals(actionType)){
            MaterialLot materialLot = requestBody.getMaterialLot();
            gcService.updateMaterialLotInfo(materialLot);
        } else if(GcMaterialLotUpdateRequest.ACTION_TYPE_UPDATE_MRB_COMMENTS.equals(actionType)){
            gcService.updateMRBComments(materialLotList, requestBody.getMrbComments());
        } else if(GcMaterialLotUpdateRequest.ACTION_TYPE_SAVE_PACKAGE_SHIPI_HIS.equals(actionType)){
            gcService.saveMLotPackageShipHis(materialLotList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
