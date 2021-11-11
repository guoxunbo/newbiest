package com.newbiest.vanchip.rest.excel.imp;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.MmsService;
import com.newbiest.ui.model.NBTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="vc客制化接口", description = "根据文件导入的信息进行查询")
public class ImportExcelController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "根据导入文件的批次号获取批次信息")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ImportExcelRequest")
    @RequestMapping(value = "/importExcelGetMLot", method = RequestMethod.POST)
    public ImportExcelResponse execute(@RequestParam MultipartFile file, @RequestParam String request) throws Exception {
        ImportExcelRequest importExcelRequest = DefaultParser.getObjectMapper().readerFor(ImportExcelRequest.class).readValue(request);
        ImportExcelResponse response = new ImportExcelResponse();
        ImportExcelResponseBody responseBody = new ImportExcelResponseBody();
        response.getHeader().setTransactionId(importExcelRequest.getHeader().getTransactionId());

        NBTable nbTable = uiService.getDeepTable(importExcelRequest.getBody().getTableRrn());
        BiMap<String, String> fieldMap = HashBiMap.create(ExcelUtils.buildHeaderByTable(nbTable, importExcelRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        List<MaterialLot> datas = (List)ExcelUtils.importExcel(getClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), ExcelUtils.DEFAULT_DATE_PATTERN);

        //根据查询条件过滤。
        List<MaterialLot> materialLots = baseService.findAll(nbTable.getModelClass(), nbTable.getWhereClause(), StringUtils.EMPTY);
        List<String> dataMLotIds = datas.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
        List<MaterialLot> materialLotList = materialLots.stream().filter(materialLot -> dataMLotIds.contains(materialLot.getMaterialLotId())).collect(Collectors.toList());

        responseBody.setMaterialLotList(materialLotList);
        response.setBody(responseBody);
        return response;
    }

}
