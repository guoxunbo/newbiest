package com.newbiest.mms.rest.materiallot.unit;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.msg.DefaultParser;
import com.newbiest.msg.Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class MaterialLotUnitController extends AbstractRestController {

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @ApiOperation(value = "对物料批单元做操作", notes = "接收。消耗。hold/release等")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotUnitRequest")
    @RequestMapping(value = "/materialLotUnitImportManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MaterialLotUnitResponse execute(@RequestBody MaterialLotUnitRequest request) throws Exception {
        MaterialLotUnitResponse response = new MaterialLotUnitResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        MaterialLotUnitResponseBody responseBody = new MaterialLotUnitResponseBody();

        MaterialLotUnitRequestBody requestBody = request.getBody();
        String errorMessage = "";
        try{
            materialLotUnitService.validateAndCreateMLotUnit(requestBody.getMaterialLotUnits());
        } catch(Exception e) {
            errorMessage = e.getMessage();
        }
        response.setMessage(errorMessage);
        return response;
    }

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotUnitRequest")
    @RequestMapping(value = "/materialLotUnitShowManage", method = RequestMethod.POST)
    public MaterialLotUnitResponse excute(@RequestParam MultipartFile file, @RequestParam String request) throws Exception {
        MaterialLotUnitRequest materialLotUnitRequest = DefaultParser.getObjectMapper().readerFor(MaterialLotUnitRequest.class).readValue(request);
        MaterialLotUnitRequestBody requestBody = materialLotUnitRequest.getBody();

        NBTable nbTable = uiService.getDeepNBTable(requestBody.getTable().getObjectRrn());
        ClassLoader classLoader = ModelFactory.getModelClassLoader(nbTable.getModelClass());
        if (classLoader == null) {
            throw new ClientParameterException(NewbiestException.COMMON_MODEL_CLASS_LOADER_IS_NOT_EXIST, nbTable.getModelClass());
        }

        BiMap<String, String> fieldMap = HashBiMap.create(ExcelUtils.buildHeaderByTable(nbTable, materialLotUnitRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        List<MaterialLotUnit> datas = (List) ExcelUtils.importExcel(classLoader.loadClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), StringUtils.EMPTY);
        MaterialLotUnitResponse response = new MaterialLotUnitResponse();
        response.getHeader().setTransactionId(materialLotUnitRequest.getHeader().getTransactionId());
        MaterialLotUnitResponseBody responseBody = new MaterialLotUnitResponseBody();
        responseBody.setMaterialLotUnits(datas);
        response.setBody(responseBody);
        return response;
    }


}
