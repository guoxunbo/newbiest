package com.newbiest.gc.rest.IncomingMaterialImport;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.utils.CsvUtils;
import com.newbiest.msg.DefaultParser;
import com.newbiest.msg.Request;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/gc")
@Slf4j
public class IncomingMaterialImportController {

    @Autowired
    UIService uiService;

    @Autowired
    GcService gcService;

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IncomingMaterialImportRequest")
    @RequestMapping(value = "/IncomingImport", method = RequestMethod.POST)
    public IncomingMaterialImportResponse excute(@RequestParam MultipartFile file, @RequestParam String request)throws Exception {
        IncomingMaterialImportRequest incomingMaterialImportRequest = DefaultParser.getObjectMapper().readerFor(IncomingMaterialImportRequest.class).readValue(request);
        IncomingMaterialImportRequestBody requestBody = incomingMaterialImportRequest.getBody();
        IncomingMaterialImportResponse response = new IncomingMaterialImportResponse();
        response.getHeader().setTransactionId(incomingMaterialImportRequest.getHeader().getTransactionId());
        IncomingMaterialImportResponseBody responseBody = new IncomingMaterialImportResponseBody();

        //验证导入模板文件名中是否包含保税属性
        String importType = requestBody.getImportType();
        String bondedProperty = "";
        if(MaterialLotUnit.FAB_SENSOR.equals(importType) || MaterialLotUnit.FAB_SENSOR_2UNMEASURED.equals(importType) || MaterialLotUnit.SENSOR_CP_KLT.equals(importType)
                || MaterialLotUnit.SENSOR_CP.equals(importType) || MaterialLotUnit.SENSOR_UNMEASURED.equals(importType) || MaterialLotUnit.SAMSUING_PACKING_LIST.equals(importType)
                || MaterialLotUnit.FAB_LCD_PTC.equals(importType) ||MaterialLotUnit.FAB_LCD_SILTERRA.equals(importType) ||MaterialLotUnit.LCD_CP_25UNMEASURED.equals(importType)
                || MaterialLotUnit.LCD_CP.equals(importType) || MaterialLotUnit.WLT_PACK_RETURN.equals(importType) || MaterialLotUnit.SENSOR_PACK_RETURN_COGO.equals(importType)
                || MaterialLotUnit.SENSOR_PACK_RETURN.equals(importType) || MaterialLotUnit.SENSOR_TPLCC.equals(importType) || MaterialLotUnit.SOC_WAFER_UNMEASURED.equals(importType)){
            bondedProperty = gcService.validationAndGetBondedPropertyByFileName(requestBody.getFileName());
        }

        NBTable nbTable = uiService.getNBTableByName(importType);

        ClassLoader classLoader = ModelFactory.getModelClassLoader(nbTable.getModelClass());
        if (classLoader == null) {
            throw new ClientParameterException(NewbiestException.COMMON_MODEL_CLASS_LOADER_IS_NOT_EXIST, nbTable.getModelClass());
        }

        BiMap<String, String> fieldMap = HashBiMap.create(CsvUtils.buildHeaderByTable(nbTable, incomingMaterialImportRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        CsvUtils.validateImportFile(fieldMap, file.getInputStream(),nbTable);
        List dataList = (List) CsvUtils.importCsv(nbTable, classLoader.loadClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), ",");
        responseBody.setDataList(dataList);
        responseBody.setBondedProperty(bondedProperty);
        response.setBody(responseBody);
        return response;
    }

    @ApiOperation(value = "importValidate", notes = "来料导入验证")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IncomingMaterialImportRequest")
    @RequestMapping(value = "/IncomingImportValidate", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IncomingMaterialImportResponse execute(@RequestBody IncomingMaterialImportRequest request) throws Exception {
        IncomingMaterialImportResponse response = new IncomingMaterialImportResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IncomingMaterialImportResponseBody responseBody = new IncomingMaterialImportResponseBody();

        IncomingMaterialImportRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        if (IncomingMaterialImportRequest.ACTION_VALIDATE_RMA.equals(actionType)) {
            List<MaterialLot> materialLots = requestBody.getMaterialLotList();
            boolean importFlag = gcService.validateRmaImportMaterialLot(materialLots);
            responseBody.setImportFlag(importFlag);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }

}
