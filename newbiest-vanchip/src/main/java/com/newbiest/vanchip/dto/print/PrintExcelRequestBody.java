package com.newbiest.vanchip.dto.print;

import com.newbiest.vanchip.dto.mes.MesRequestBody;
import com.newbiest.vanchip.dto.print.model.*;
import lombok.Data;

@Data
public class PrintExcelRequestBody extends MesRequestBody {

    private Object object;

    private String actionType;

    private CocPrintInfo cocPrintInfo;

    private PackingListPrintInfo packingListPrintInfo;

    private ShippingListPrintInfo shippingListPrintInfo;

    private PKListPrintInfo pKListPrintInfo;

    private ExcelPrintInfo excelPrintInfo;

}
