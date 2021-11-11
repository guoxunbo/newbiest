package com.newbiest.vanchip.rest.excel.imp;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ImportExcelResponse extends Response {

    private ImportExcelResponseBody body;
}
