package com.newbiest.gc.rest.IncomingMaterialImport;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class IncomingMaterialImportResponse  extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialImportResponseBody body;
}
