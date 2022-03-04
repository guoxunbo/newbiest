package com.newbiest.gc.temp.rest.ft;

import com.google.common.collect.Maps;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.gc.scm.dto.TempFtModel;
import com.newbiest.gc.service.TempFtService;
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
 * 接收FT老系统的数据
 */
@RestController
@RequestMapping("/gc")
@Slf4j
public class TempFtController extends AbstractRestController {

    @Autowired
    TempFtService tempFtService;

    @RequestMapping(value = "/tempFtDataImport", method = RequestMethod.POST)
    public TempFtResponse importData(@RequestParam MultipartFile file, @RequestParam String request) throws Exception {

        TempFtRequest tempRequest = DefaultParser.getObjectMapper().readerFor(TempFtRequest.class).readValue(request);

        TempFtResponse response = new TempFtResponse();
        Map<String, String> fieldMap = Maps.newHashMap();
        fieldMap.put("WAFER_ID", "waferId");//真空包号或者Wafer号
        fieldMap.put("BOX_ID", "boxId");//B、SBB箱号，其他中转箱号
        fieldMap.put("WAFER_TYPE", "grade");//grade
        fieldMap.put("STOCK_ID", "stockId");//仓库
        fieldMap.put("POINT_ID", "pointId");//库位号
        fieldMap.put("WAFER_NUM", "waferNum");//棵树currentQty
        fieldMap.put("IN_STOCK_TIME", "inTime");//createTime
        fieldMap.put("PRODUCT_ID", "productId");//material_name  根据waferSource粉去分形态-4，-3.5
        fieldMap.put("WAFER_SOURCE", "waferSource");//类别区分，用于区分reserved7、reserved49、reserved50
        fieldMap.put("SECOND_CODE", "secondCode");
        fieldMap.put("LOCATION", "location");
        fieldMap.put("VENDER", "vendor");//reserved22
        fieldMap.put("PASS_NUM", "passNum");//reserved34
        fieldMap.put("NG_NUM", "ngNum");//reserved35
        fieldMap.put("LOT_ID", "lotId");//lotId不为空为wafer
        fieldMap.put("PO_NO", "poNo");//reserved27
        fieldMap.put("WO_ID", "woId");//reserved46
        fieldMap.put("FAB_DEVICE", "fabDevice");//reserved24
        fieldMap.put("CARTONNO", "cartonNo");//reserved39
        fieldMap.put("PACK_DEVICE", "packDevice");//新增栏位记录
        fieldMap.put("YIELD", "yield");//reserved36
        fieldMap.put("REMARK", "remark");//reserved41
        fieldMap.put("INVOICEID", "invoiceId");//reserved29
        fieldMap.put("PACK_LOTID", "packLotId");//reserved37
        fieldMap.put("DATA_VAL3", "dataValue3");//工程师名
        fieldMap.put("DATA_VAL4", "dataValue4");//实验目的
        fieldMap.put("DATA_VAL5", "dataValue5");//工程备注
        fieldMap.put("DATA_VAL8", "dataValue8");//0:stockIn 1:hold 其他：create
        fieldMap.put("DATA_VAL12", "dataValue12");//装箱检验  Y：已经检验
        fieldMap.put("DATA_VAL13", "dataValue13");//出货检验  Y：以检验
        fieldMap.put("DATA_VAL14", "dataValue14");//ENG/PROD   product_type
        fieldMap.put("DATA_VAL16", "dataValue16");//reserved38
        fieldMap.put("DATA_VAL19", "dataValue19");//reserved33
        fieldMap.put("DATA_VAL20", "dataValue20");//reserved42
        fieldMap.put("CST_ID", "cstId");//durable和lot_Cst两个栏位
        fieldMap.put("SALEREMARK_DESC", "saleRemarkDesc");//reserved3
        fieldMap.put("PRODREMARK_DESC", "prodRemarkDesc");//reserved4
        fieldMap.put("HOLD_DESC", "holdDesc");//holdReason
        fieldMap.put("MATERIAL_ID", "materialId");//materialCode
        fieldMap.put("VQR_ID", "vqrId");//VBOX_QRCODE_INFO二维码信息
        fieldMap.put("BQR_ID", "bqrId");//BOX_QRCODE_INFO
        fieldMap.put("DATA_VAL24", "dataValue24");//reserved43
        fieldMap.put("DATA_VAL25", "dataValue25");//reserved45
        fieldMap.put("DATA_VAL29", "dataValue29");//原产品型号SOURCE_PRODUCT_ID


        List<TempFtModel> datas = (List) ExcelUtils.importExcel(TempFtModel.class, fieldMap, file.getInputStream(), TempFtRequest.DEFAULT_DATE_PATTERN);

        tempFtService.transferFtData(datas, file.getOriginalFilename());
        response.getHeader().setTransactionId(tempRequest.getHeader().getTransactionId());

        return response;
    }

}
