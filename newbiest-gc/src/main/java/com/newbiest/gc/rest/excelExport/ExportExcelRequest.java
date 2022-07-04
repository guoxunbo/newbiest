package com.newbiest.gc.rest.excelExport;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ExportExcelRequest extends Request {

    private ExportExcelRequestBody body;
}
