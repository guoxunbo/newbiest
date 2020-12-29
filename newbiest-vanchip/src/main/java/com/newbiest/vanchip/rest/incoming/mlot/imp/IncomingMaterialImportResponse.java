package com.newbiest.vanchip.rest.incoming.mlot.imp;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IncomingMaterialImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialImportResponseBody body;
}
