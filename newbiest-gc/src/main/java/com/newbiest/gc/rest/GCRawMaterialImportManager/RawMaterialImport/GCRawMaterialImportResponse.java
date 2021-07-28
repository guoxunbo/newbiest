package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialImport;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class GCRawMaterialImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private GCRawMaterialImportResponseBody body;
}
