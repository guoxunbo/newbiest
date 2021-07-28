package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialImport;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.mms.utils.CsvUtils;
import com.newbiest.msg.DefaultParser;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/gc")
@Slf4j
public class GCRawMaterialImportController {

    @Autowired
    UIService uiService;

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GCRawMaterialImportRequest")
    @RequestMapping(value = "/RawMaterialImport", method = RequestMethod.POST)
    public GCRawMaterialImportResponse excute(@RequestParam MultipartFile file, @RequestParam String request)throws Exception {
        GCRawMaterialImportRequest rawMaterialImportRequest = DefaultParser.getObjectMapper().readerFor(GCRawMaterialImportRequest.class).readValue(request);
        GCRawMaterialImportResponse response = new GCRawMaterialImportResponse();
        response.getHeader().setTransactionId(rawMaterialImportRequest.getHeader().getTransactionId());
        GCRawMaterialImportResponseBody responseBody = new GCRawMaterialImportResponseBody();

        NBTable nbTable = uiService.getNBTableByName(GCRawMaterialImportRequest.NB_TABLE_NAME);
        ClassLoader classLoader = ModelFactory.getModelClassLoader(nbTable.getModelClass());
        if (classLoader == null) {
            throw new ClientParameterException(NewbiestException.COMMON_MODEL_CLASS_LOADER_IS_NOT_EXIST, nbTable.getModelClass());
        }
        BiMap<String, String> fieldMap = HashBiMap.create(CsvUtils.buildHeaderByTable(nbTable, rawMaterialImportRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        CsvUtils.validateImportFile(fieldMap, file.getInputStream(), nbTable);
        List dataList = (List) CsvUtils.importCsv(nbTable, classLoader.loadClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), ",");
        responseBody.setDataList(dataList);
        response.setBody(responseBody);
        return response;
    }

}
