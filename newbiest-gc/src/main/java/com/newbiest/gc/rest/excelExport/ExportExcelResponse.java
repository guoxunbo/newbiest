package com.newbiest.gc.rest.excelExport;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class ExportExcelResponse extends Response {

    private static final long serialVersionUID = 1L;

    private ExportExcelResponseBody body;
}
