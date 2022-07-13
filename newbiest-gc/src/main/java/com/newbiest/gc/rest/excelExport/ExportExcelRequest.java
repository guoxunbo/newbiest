package com.newbiest.gc.rest.excelExport;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ExportExcelRequest extends Request {

    public static final String ACTION_EXT_COB_DATA = "ExpCobData";
    public static final String ACTION_EXT_COB_UNIT_DATA= "ExpCobUnitData";
    public static final String ACTION_EXT_COB_PREVIEW_DATA= "ExpCobPreviewData";

    private ExportExcelRequestBody body;
}
