package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialDelete;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class IncomingMaterialDeleteResponse extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialDeleteResponseBody body;
}
