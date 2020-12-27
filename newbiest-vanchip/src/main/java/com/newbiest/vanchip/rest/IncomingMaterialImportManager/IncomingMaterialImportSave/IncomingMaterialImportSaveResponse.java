package com.newbiest.vanchip.rest.IncomingMaterialImportManager.IncomingMaterialImportSave;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IncomingMaterialImportSaveResponse extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialImportSaveResponseBody body;
}
