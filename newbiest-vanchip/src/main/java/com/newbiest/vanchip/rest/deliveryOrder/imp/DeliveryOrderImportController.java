package com.newbiest.vanchip.rest.deliveryOrder.imp;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.utils.CsvUtils;
import com.newbiest.ui.model.NBTable;
import com.newbiest.vanchip.service.VanChipService;
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

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip", description = "Vanchip客制化接口")
public class DeliveryOrderImportController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "发货通知单导入")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "DeliveryOrderImportRequest")
    @RequestMapping(value = "/deliveryOrderImport", method = RequestMethod.POST)
    public DeliveryOrderImportResponse excute(@RequestParam MultipartFile file, @RequestParam String request) throws Exception {
        DeliveryOrderImportRequest shipmentOrderImportRequest = DefaultParser.getObjectMapper().readerFor(DeliveryOrderImportRequest.class).readValue(request);

        DeliveryOrderImportResponse response = new DeliveryOrderImportResponse();
        response.getHeader().setTransactionId(shipmentOrderImportRequest.getHeader().getTransactionId());
        DeliveryOrderImportResponseBody responseBody = new DeliveryOrderImportResponseBody();

        String importTypeNBTable = shipmentOrderImportRequest.getBody().getImportTypeNbTable();
        NBTable nbTable = uiService.getTableByName(importTypeNBTable);

        BiMap<String, String> fieldMap = HashBiMap.create(CsvUtils.buildHeaderByTable(nbTable, shipmentOrderImportRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        CsvUtils.validateImportFile(fieldMap, file.getInputStream(), nbTable);
        List dataList = (List) CsvUtils.importCsv(nbTable, getClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), StringUtils.SPLIT_COMMA);
        responseBody.setDataList(dataList);
        response.setBody(responseBody);
        return response;
    }
}

