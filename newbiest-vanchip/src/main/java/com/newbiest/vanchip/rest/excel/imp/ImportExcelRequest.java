package com.newbiest.vanchip.rest.excel.imp;

import com.newbiest.base.msg.Request;
import lombok.Data;

@Data
public class ImportExcelRequest extends Request {

    public static final String MESSAGE_NAME = "ImportExcel";

    public static final String ACTION_TYPE_GET_MLOT_LIST_BY_MLOT_ID = "GetMaterialLot";

    private ImportExcelRequestBody body;
}
