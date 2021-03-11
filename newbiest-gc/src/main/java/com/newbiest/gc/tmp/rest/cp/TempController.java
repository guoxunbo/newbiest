package com.newbiest.gc.tmp.rest.cp;

import com.google.common.collect.Maps;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.gc.scm.dto.TempCpModel;
import com.newbiest.gc.service.TempService;
import com.newbiest.msg.DefaultParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 临时的controller
 * 接收老系统的数据
 */
@RestController
@RequestMapping("/gc")
@Slf4j
public class TempController extends AbstractRestController {

    @Autowired
    TempService tempService;

    @RequestMapping(value = "/tempCpDataImport", method = RequestMethod.POST)
    public TempResponse importData(@RequestParam MultipartFile file, @RequestParam String request) throws Exception {
        TempRequest tempRequest = DefaultParser.getObjectMapper().readerFor(TempRequest.class).readValue(request);

        TempResponse response = new TempResponse();
        Map<String, String> fieldMap = Maps.newHashMap();
        fieldMap.put("WAFER_ID", "waferId");
        fieldMap.put("BOX_ID", "boxId");
        fieldMap.put("WAFER_TYPE", "waferType");
        fieldMap.put("STOCK_ID", "stockId");
        fieldMap.put("POINT_ID", "pointId");
        fieldMap.put("IN_STOCK_TIME", "inTime");
        fieldMap.put("SECOND_CODE", "secondCode");
        fieldMap.put("LOCATION", "location");
        fieldMap.put("VENDER", "vendor");
        fieldMap.put("LOT_ID", "lotId");
        fieldMap.put("PO_NO", "poNo");
        fieldMap.put("WO_ID", "woId");
        fieldMap.put("FAB_DEVICE", "fabDevice");
        fieldMap.put("CARTONNO", "cartonNo");
        fieldMap.put("INVOICEID", "invoiceId");
        fieldMap.put("DATA_VAL5", "dataValue5");
        fieldMap.put("DATA_VAL6", "dataValue6");
        fieldMap.put("DATA_VAL7", "dataValue7");
        fieldMap.put("DATA_VAL8", "dataValue8");
        fieldMap.put("DATA_VAL18", "dataValue18");
        fieldMap.put("CST_WAFERQTY", "cstWaferQty");
        fieldMap.put("PRODREMARK_DESC", "prodRemarkDesc");

        List<TempCpModel> datas = (List) ExcelUtils.importExcel(TempCpModel.class, fieldMap, file.getInputStream(), TempRequest.DEFAULT_DATE_PATTERN);

        tempService.transferCpData(datas, file.getOriginalFilename());
        response.getHeader().setTransactionId(tempRequest.getHeader().getTransactionId());

        return response;
    }

}
