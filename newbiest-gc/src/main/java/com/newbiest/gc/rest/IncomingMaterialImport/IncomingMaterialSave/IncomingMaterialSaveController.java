package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialSave;

import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
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
@RequestMapping("/gc")
@Slf4j
public class IncomingMaterialSaveController {

    @Autowired
    BaseService baseService;

    @Autowired
    UIService uiService;

    @Autowired
    GcService gcService;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @ApiOperation(value = "来料导入数据保存", notes = "save")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IncomingMaterialImportRequest")
    @RequestMapping(value = "/IncomingMaterialSave", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IncomingMaterialSaveResponse excute(@RequestBody IncomingMaterialSaveRequest request) throws Exception {
        IncomingMaterialSaveResponse response = new IncomingMaterialSaveResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IncomingMaterialSaveResponseBody responseBody = new IncomingMaterialSaveResponseBody();
        IncomingMaterialSaveRequestBody requestBody = request.getBody();

        String importType = requestBody.getImportType();
        String checkFourCodeFlag = requestBody.getCheckFourCodeFlag();

        String importCode = "";
        if(MaterialLotUnit.COB_FINISH_PRODUCT.equals(importType) || MaterialLotUnit.SOC_FINISH_PRODUCT.equals(importType) || MaterialLotUnit.COB_RAW_MATERIAL_PRODUCT.equals(importType)){
            List<MaterialLotUnit> materialLotUnitList = requestBody.getMaterialLotUnitList();
            materialLotUnitList = gcService.validateAndSetWaferSource(importType, checkFourCodeFlag, materialLotUnitList);
            materialLotUnitList = materialLotUnitService.createMLot(materialLotUnitList);
            importCode = materialLotUnitList.get(0).getReserved48();
        } else if(MaterialLotUnit.SAMSUING_PACKING_LIST.equals(importType) || MaterialLotUnit.LCD_COG_FINISH_PRODUCT.equals(importType)
                || MaterialLotUnit.SENSOR_RMA_GOOD_PRODUCT.equals(importType) || MaterialLotUnit.WLT_RMA_GOOD_PRODUCT.equals(importType) || MaterialLotUnit.RMA_RETURN.equals(importType) || MaterialLotUnit.RMA_PURE.equals(importType)){
            List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
            importCode = gcService.saveIncomingMaterialList(materialLotList, importType);
        } else if(MaterialLotUnit.LCD_COG_DETIAL.equals(importType)){
            List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
            importCode = gcService.saveLCDCOGDetailList(materialLotList, importType);
        } else if(MaterialLotUnit.WLT_PACK_RETURN.equals(importType)){
            List<MaterialLotUnit> materialLotUnitList = requestBody.getMaterialLotUnitList();
            materialLotUnitList = gcService.materialLotUnitAssignEng(materialLotUnitList);
            materialLotUnitList = gcService.validateImportWltPackReturn(materialLotUnitList);
            importCode = materialLotUnitList.get(0).getReserved48();
        } else if(MaterialLotUnit.SENSOR_PACK_RETURN.equals(importType)){
            List<MaterialLotUnit> materialLotUnitList = requestBody.getMaterialLotUnitList();
            materialLotUnitList = gcService.validateAndChangeMaterialNameByImportType(materialLotUnitList, importType);
            materialLotUnitList = gcService.materialLotUnitAssignEng(materialLotUnitList);
            materialLotUnitList = gcService.createFTMaterialLotAndGetImportCode(materialLotUnitList, importType);
            importCode = materialLotUnitList.get(0).getReserved48();
        } else {
            List<MaterialLotUnit> materialLotUnitList = requestBody.getMaterialLotUnitList();
            materialLotUnitList = materialLotUnitService.getMaterialLotUnitByFabLotAndWaferId(materialLotUnitList, importType);
            materialLotUnitList = gcService.validateAndSetWaferSource(importType, checkFourCodeFlag, materialLotUnitList);
            gcService.validateMLotUnitProductAndSubcode(materialLotUnitList);
            materialLotUnitList = gcService.validateAndChangeMaterialNameByImportType(materialLotUnitList, importType);
            materialLotUnitList = gcService.materialLotUnitAssignEng(materialLotUnitList);
            materialLotUnitList = materialLotUnitService.createMLot(materialLotUnitList);
            importCode = materialLotUnitList.get(0).getReserved48();
        }
        responseBody.setImportCode(importCode);
        response.setBody(responseBody);
        return response;
    }
}
