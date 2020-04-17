package com.newbiest.gc.rest.IncomingMaterialImport;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.utils.CsvUtils;
import com.newbiest.msg.DefaultParser;
import io.swagger.annotations.ApiImplicitParam;
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
        String bondedProperty = gcService.validationAndGetBondedPropertyByFileName(requestBody.getFileName());

        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(MaterialLot.INCOMING_MLOT_IMPORTTYPE, NBReferenceList.CATEGORY_OWNER);
        String importType = requestBody.getImportType();
        String nbTableName = "";
        for(NBReferenceList nbReferenceListInfo : nbReferenceList){
            if(importType.equals(nbReferenceListInfo.getValue())){
                nbTableName = nbReferenceListInfo.getDescription();
            }
        }
        NBTable nbTable = uiService.getNBTableByName(nbTableName);

        ClassLoader classLoader = ModelFactory.getModelClassLoader(nbTable.getModelClass());
        if (classLoader == null) {
            throw new ClientParameterException(NewbiestException.COMMON_MODEL_CLASS_LOADER_IS_NOT_EXIST, nbTable.getModelClass());
        }

        BiMap<String, String> fieldMap = HashBiMap.create(CsvUtils.buildHeaderByTable(nbTable, incomingMaterialImportRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        CsvUtils.validateImportFile(fieldMap, file.getInputStream());
        List dataList = (List) CsvUtils.importCsv(nbTable, classLoader.loadClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), ",");
        responseBody.setDataList(dataList);
        responseBody.setBondedProperty(bondedProperty);
        response.setBody(responseBody);
        return response;
    }

}
