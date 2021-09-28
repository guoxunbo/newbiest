package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.vanchip.dto.print.model.*;

/**
 * 小程序打印
 */
@Deprecated
public interface PrintExcelService {
    void printExcel(ExcelPrintInfo excelPrintInfo) throws ClientException;
    void printExcel(CocPrintInfo cocPrintInfo, PackingListPrintInfo packingListPrintInfo, ShippingListPrintInfo shippingListPrintInfo, PKListPrintInfo pKListPrintInfo, String actionType) throws ClientException;
}
