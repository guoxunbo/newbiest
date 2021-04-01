package com.newbiest.vanchip.rest.csv;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class CsvImportRequest extends Request {


    public static final String MESSAGE_NAME = "CsvImport";

    private CsvImportRequestBody body;
}
