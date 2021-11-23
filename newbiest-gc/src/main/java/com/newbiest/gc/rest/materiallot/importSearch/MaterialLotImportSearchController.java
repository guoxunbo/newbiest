package com.newbiest.gc.rest.materiallot.importSearch;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.model.GCProductSubcode;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.DefaultParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by guozhangLuo
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "物料批次导入查询")
public class MaterialLotImportSearchController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotImportSearchRequest")
    @RequestMapping(value = "/mLotImportSearch", method = RequestMethod.POST)
    public MaterialLotImportSearchResponse excute(@RequestParam MultipartFile file, @RequestParam String request) throws Exception {
        MaterialLotImportSearchRequest mLotImportSearchRequest = DefaultParser.getObjectMapper().readerFor(MaterialLotImportSearchRequest.class).readValue(request);
        MaterialLotImportSearchRequestBody requestBody = mLotImportSearchRequest.getBody();
        MaterialLotImportSearchResponse response = new MaterialLotImportSearchResponse();
        response.getHeader().setTransactionId(mLotImportSearchRequest.getHeader().getTransactionId());
        MaterialLotImportSearchResponseBody responseBody = new MaterialLotImportSearchResponseBody();

        NBTable nbTable = uiService.getDeepNBTable(requestBody.getTableRrn());
        ClassLoader classLoader = ModelFactory.getModelClassLoader(nbTable.getModelClass());
        if (classLoader == null) {
            throw new ClientParameterException(NewbiestException.COMMON_MODEL_CLASS_LOADER_IS_NOT_EXIST, nbTable.getModelClass());
        }

        BiMap<String, String> fieldMap = HashBiMap.create(ExcelUtils.buildHeaderByTable(nbTable, mLotImportSearchRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        List<MaterialLot> materialLotList = (List) ExcelUtils.importExcel(classLoader.loadClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), StringUtils.EMPTY);
        if(CollectionUtils.isNotEmpty(materialLotList)){
            List<MaterialLot> materialLots = gcService.getMaterialLotsByImportFileAndNbTable(materialLotList, nbTable);
            responseBody.setMaterialLotList(materialLots);
        }
        response.setBody(responseBody);
        return response;
    }

}
