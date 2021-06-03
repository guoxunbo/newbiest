package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.vanchip.dto.print.model.CocPrintInfo;
import com.newbiest.vanchip.dto.print.model.PKListPrintInfo;
import com.newbiest.vanchip.dto.print.model.PackingListPrintInfo;
import com.newbiest.vanchip.dto.print.model.ShippingListPrintInfo;

/**
 * 小程序打印
 */
@Deprecated
public interface PrintExcelService {

    void printExcel(CocPrintInfo cocPrintInfo, PackingListPrintInfo packingListPrintInfo, ShippingListPrintInfo shippingListPrintInfo, PKListPrintInfo pKListPrintInfo, String actionType) throws ClientException;
}
