package com.newbiest.vanchip.rest.IncomingMaterialImportManager.IncomingMaterialImport;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IncomingMaterialImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialImportResponseBody body;
}
