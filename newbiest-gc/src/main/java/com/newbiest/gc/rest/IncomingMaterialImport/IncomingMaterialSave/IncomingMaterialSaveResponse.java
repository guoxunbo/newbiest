package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialSave;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class IncomingMaterialSaveResponse extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialSaveResponseBody body;
}
