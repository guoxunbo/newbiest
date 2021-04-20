package com.newbiest.gc.rest.rw.manager.rw.material.manager;

import com.newbiest.base.exception.ClientException;
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

@RestController("RwMaterialController")
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class RwMaterialController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "RwMaterialManager", notes = "RW辅料管理")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RwMaterialRequest")
    @RequestMapping(value = "/rwMaterialManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RwMaterialResponse execute(@RequestBody RwMaterialRequest request) throws Exception {
        RwMaterialResponse response = new RwMaterialResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        RwMaterialResponseBody responseBody = new RwMaterialResponseBody();
        RwMaterialRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if (RwMaterialRequest.ACTION_TAPE_SCAN.equals(actionType)) {
            List<MaterialLot> materialLotList = gcService.getMaterialLotByTapeMaterialCode(requestBody.getTapeMaterialCode());
            responseBody.setMaterialLotList(materialLotList);
        } else if(RwMaterialRequest.ACTION_TAPE_RECEIVE.equals(actionType)) {
            gcService.receiveTapeMaterial(requestBody.getMaterialLotList(), requestBody.getTapeSize());
        } else if(RwMaterialRequest.ACTION_GET_BLADE_MLOTID.equals(actionType)){
            String materialLotId = gcService.validateAndGetBladeMLotId(requestBody.getMaterialLotCode());
            responseBody.setMaterialLotId(materialLotId);
        } else if(RwMaterialRequest.ACTION_BLADE_SCAN.equals(actionType)){
            MaterialLot materialLot = gcService.getMaterialLotByBladeMaterialCode(requestBody.getBladeMaterialCode());
            responseBody.setMaterialLot(materialLot);
        } else if(RwMaterialRequest.ACTION_BLADE_RECEIVE.equals(actionType)){
            gcService.receiveBladeMaterial(requestBody.getMaterialLotList());
        } else if(RwMaterialRequest.ACTION_MATERIAL_SPARE.equals(actionType)){
            gcService.spareRwMaterial(requestBody.getMaterialLotList());
        } else if(RwMaterialRequest.ACTION_MATERIAL_ISSUE.equals(actionType)){
            gcService.issueRwMaterial(requestBody.getMaterialLotList());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
