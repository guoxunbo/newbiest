package com.newbiest.vanchip.rest.storage.imp;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.mms.model.Storage;
import com.newbiest.ui.model.NBTable;
import com.newbiest.vanchip.service.VanChipService;
import io.swagger.annotations.Api;
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
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class StorageImportController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StorageImportRequest")
    @RequestMapping(value = "/storageImport", method = RequestMethod.POST)
    public StorageImportResponse excute(@RequestParam MultipartFile file, @RequestParam String request)throws Exception {
        StorageImportRequest storageImportRequest = DefaultParser.getObjectMapper().readerFor(StorageImportRequest.class).readValue(request);
        StorageImportResponse response = new StorageImportResponse();
        response.getHeader().setTransactionId(storageImportRequest.getHeader().getTransactionId());
        StorageImportResponseBody responseBody = new StorageImportResponseBody();

        NBTable nbTable = uiService.getDeepTable(storageImportRequest.getBody().getObjectRrn());
        BiMap<String, String> fieldMap = HashBiMap.create(ExcelUtils.buildHeaderByTable(nbTable, storageImportRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        List<Storage> datas = (List)ExcelUtils.importExcel(getClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), "");

        List<Storage> storageList = Lists.newArrayList();
        for (Storage storage : datas) {
            storage = vanChipService.saveStorageInfo(storage);
            storageList.add(storage);
        }

        responseBody.setDataList(storageList);
        response.setBody(responseBody);
        return response;
    }

}
