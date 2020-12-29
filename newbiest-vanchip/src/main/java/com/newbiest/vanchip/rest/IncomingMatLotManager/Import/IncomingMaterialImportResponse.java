package com.newbiest.vanchip.rest.IncomingMatLotManager.Import;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IncomingMaterialImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialImportResponseBody body;
}
