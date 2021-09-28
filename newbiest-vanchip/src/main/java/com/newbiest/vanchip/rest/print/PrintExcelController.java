package com.newbiest.vanchip.rest.print;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.vanchip.dto.print.model.*;
import com.newbiest.vanchip.service.PrintExcelService;
import com.newbiest.vanchip.service.VanChipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="excel打印", description = "暂时使用此种方式去打印")
public class PrintExcelController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    PrintExcelService printExcelService;

    @ApiOperation(value = "打印标签")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "PrintExcelRequest")
    @RequestMapping(value = "/printExcel", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public PrintExcelResponse execute(@RequestBody PrintExcelRequest request) throws Exception {
        PrintExcelResponse response = new PrintExcelResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        PrintExcelResponseBody responseBody = new PrintExcelResponseBody();
        PrintExcelRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (PrintExcelRequest.ACTION_PRINT_COC.equals(actionType)){
            CocPrintInfo cocPrintParameter = vanChipService.getCOCPrintParameter(requestBody.getDocumentLineId());
            printExcelService.printExcel(cocPrintParameter, null, null, null, PrintExcelRequest.ACTION_PRINT_COC);
        }else if(PrintExcelRequest.ACTION_PRINT_PACKING_LIST.equals(actionType)){

            PackingListPrintInfo packingListPrintInfo = vanChipService.getPackingListPrintParameter(requestBody.getDocumentLineId());
            printExcelService.printExcel(null, packingListPrintInfo, null, null,PrintExcelRequest.ACTION_PRINT_PACKING_LIST);
        }else if(PrintExcelRequest.ACTION_PRINT_SHIPPING_LIST.equals(actionType)){

            ShippingListPrintInfo shippingListPrintInfo = vanChipService.getShippingListPrintParameter(requestBody.getDocumentLineId());
            printExcelService.printExcel(null,null, shippingListPrintInfo, null, PrintExcelRequest.ACTION_PRINT_SHIPPING_LIST);
        }else if(PrintExcelRequest.ACTION_PRINT_PK_LIST.equals(actionType)){
            PKListPrintInfo pKListPrintInfo = vanChipService.getPKListParameter(requestBody.getDocumentLineId());
            printExcelService.printExcel(null,null, null, pKListPrintInfo, PrintExcelRequest.ACTION_PRINT_PK_LIST);
        }else if (PrintExcelRequest.ACTION_PRINT_PACKING_LIST_AND_COC.equals(actionType)){
            CocPrintInfo cocPrintParameter = vanChipService.getCOCPrintParameter(requestBody.getDocumentLineId());
            PackingListPrintInfo packingListPrintInfo = vanChipService.getPackingListPrintParameter(requestBody.getDocumentLineId());
            ExcelPrintInfo excelPrintInfo = new ExcelPrintInfo();
            excelPrintInfo.setCocPrintInfo(cocPrintParameter);
            excelPrintInfo.setPackingListPrintInfo(packingListPrintInfo);

            printExcelService.printExcel(cocPrintParameter, packingListPrintInfo, null, null, PrintExcelRequest.ACTION_PRINT_PACKING_LIST_AND_COC);
        }else if (PrintExcelRequest.ACTION_PRINT_DELIVERY_ORDER.equals(actionType)){
            DeliveryPrintInfo deliveryPrintInfo = vanChipService.getDeliveryOrderParameter(requestBody.getDocumentLineId());

            ExcelPrintInfo excelPrintInfo = new ExcelPrintInfo();
            excelPrintInfo.setDeliveryPrintInfo(deliveryPrintInfo);
            excelPrintInfo.setActionType(actionType);
            printExcelService.printExcel(excelPrintInfo);

        }else if (PrintExcelRequest.ACTION_PRINT_RS_AND_SCRAP_ORDER.equals(actionType)){
            RSAndScrapInfo rsAndScrapInfo = vanChipService.getReturnSuppleOrderAndScrapOrderPrintInfo(requestBody.getDocumentLineId());
            ExcelPrintInfo excelPrintInfo = new ExcelPrintInfo();
            excelPrintInfo.setRSAndScrapInfo(rsAndScrapInfo);
            excelPrintInfo.setActionType(actionType);
            printExcelService.printExcel(excelPrintInfo);
        }else{
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);

        }
        response.setBody(responseBody);
        return response;
    }

}
