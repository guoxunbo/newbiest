package com.newbiest.vanchip.rest.incoming.mlot.imp;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.utils.CsvUtils;
import com.newbiest.ui.model.NBTable;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/vc")
@Slf4j
public class IncomingMaterialImportController extends AbstractRestController {

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IncomingMaterialImportRequest")
    @RequestMapping(value = "/IncomingMaterialImport", method = RequestMethod.POST)
    public IncomingMaterialImportResponse excute(@RequestParam MultipartFile file, @RequestParam String request)throws Exception {
        IncomingMaterialImportRequest rawMaterialImportRequest = DefaultParser.getObjectMapper().readerFor(IncomingMaterialImportRequest.class).readValue(request);
        IncomingMaterialImportResponse response = new IncomingMaterialImportResponse();
        response.getHeader().setTransactionId(rawMaterialImportRequest.getHeader().getTransactionId());
        IncomingMaterialImportResponseBody responseBody = new IncomingMaterialImportResponseBody();

        String importTypeNBTable = rawMaterialImportRequest.getBody().getImportTypeNbTable();
        NBTable nbTable = uiService.getTableByName(importTypeNBTable);

        BiMap<String, String> fieldMap = HashBiMap.create(CsvUtils.buildHeaderByTable(nbTable, rawMaterialImportRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        CsvUtils.validateImportFile(fieldMap, file.getInputStream(), nbTable);
        List dataList = (List) CsvUtils.importCsv(nbTable, getClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), StringUtils.SPLIT_COMMA);
        responseBody.setDataList(dataList);
        response.setBody(responseBody);
        return response;
    }

}
