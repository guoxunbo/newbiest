package com.newbiest.gc.rest.IncomingMaterialImport.HNWarehouseImport;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class HNWarehouseImportResponse  extends Response {

    private static final long serialVersionUID = 1L;

    private HNWarehouseImportResponseBody body;
}
