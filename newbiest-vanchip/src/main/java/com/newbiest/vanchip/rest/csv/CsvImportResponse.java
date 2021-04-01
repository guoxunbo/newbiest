package com.newbiest.vanchip.rest.csv;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class CsvImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private CsvImportResponseBody body;
}
