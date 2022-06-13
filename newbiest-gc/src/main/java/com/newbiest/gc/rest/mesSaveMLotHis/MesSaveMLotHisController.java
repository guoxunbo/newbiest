package com.newbiest.gc.rest.mesSaveMLotHis;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MmsService;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlaxyCore MES记录MLot历史
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class MesSaveMLotHisController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "物料批记录历史", notes = "记录物料、晶圆历史")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MesSaveMLotHisRequest")
    @RequestMapping(value = "/mLotSaveHisManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MesSaveMLotHisResponse execute(@RequestBody MesSaveMLotHisRequest request) throws Exception {
        MesSaveMLotHisResponse response = new MesSaveMLotHisResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MesSaveMLotHisResponseBody responseBody = new MesSaveMLotHisResponseBody();

        MesSaveMLotHisRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        String transId = requestBody.getTransId();
        String errorMessage = "";
        if (MesSaveMLotHisRequest.ACTION_SAVE_MLOTUNIT_HIS.equals(actionType)) {
            List<MaterialLotUnit> materialLotUnitList = requestBody.getMaterialLotUnits();
            try {
                gcService.mesSaveMaterialLotUnitHis(materialLotUnitList, transId);
            } catch (Exception e){
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_RECEIVE_RAW_MATERIAL.equals(actionType)){
            List<MaterialLot> materialLotList = requestBody.getMaterialLots();
            try {
                gcService.mesReceiveRawMaterialAndSaveHis(materialLotList, transId);
            } catch (Exception e){
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_IRA_RETURN.equals(actionType)){
            List<MaterialLot> materialLotList = requestBody.getMaterialLots();
            try {
                gcService.mesRawMaterialReturnWarehouse(materialLotList, transId, Material.MATERIAL_TYPE_IRA);
            } catch (Exception e){
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_GLUE_RETURN.equals(actionType)){
            try {
                gcService.mesRawMaterialReturnWarehouse(requestBody.getMaterialLots(), transId, Material.MATERIAL_TYPE_GLUE);
            } catch (Exception e){
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_WIRE_RETURN.equals(actionType)){
            try {
                gcService.mesRawMaterialReturnWarehouse(requestBody.getMaterialLots(), transId, Material.MATERIAL_TYPE_GOLD);
            } catch (Exception e){
            }
        } else if(MesSaveMLotHisRequest.ACTION_BIND_WORKORDER.equals(actionType)){
            try {
                gcService.mesMaterialLotBindWorkOrderAndSaveHis(requestBody.getMaterialLots(), transId);
            } catch (Exception e){
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_BIND_WAFER_WORKORDER.equals(actionType)){
            try {
                gcService.mesMaterialLotUnitBindWorkorderAndSaveHis(requestBody.getMaterialLotUnits(), transId);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_UN_BIND_WAFER_WORKORDER.equals(actionType)){
            try {
                gcService.mesMaterialLotUnitUnBindWorkorderAndSaveHis(requestBody.getMaterialLotUnits(), transId);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_UN_BIND_MLOT_WORKORDER.equals(actionType)){
            try {
                List<MaterialLotUnit> materialLotUnits = requestBody.getMaterialLotUnits();
                List<MaterialLot> materialLots = materialLotUnits.stream().map(materialLotUnit -> mmsService.getMLotByMLotId(materialLotUnit.getMaterialLotId(), true)).collect(Collectors.toList());
                gcService.mesMaterialLotUnBindWorkorderAndSaveHis(materialLots, transId);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_UN_RECON_MLOT_UNIT.equals(actionType)){
            try {
                gcService.reconMaterialLotUnitAndSaveHis(requestBody.getMaterialLotUnits(), transId);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
        } else if(MesSaveMLotHisRequest.ACTION_LSW_MLOT_UNIT_ENDHOLD_.equals(actionType)){
            try {
                gcService.lswMaterialLotUnitEngHoldAndSaveHis(requestBody.getMaterialLotUnits(), transId);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setMessage(errorMessage);
        return response;
    }

}
