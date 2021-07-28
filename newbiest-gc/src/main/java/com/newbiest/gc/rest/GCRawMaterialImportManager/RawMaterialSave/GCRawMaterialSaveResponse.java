package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class GCRawMaterialSaveResponse extends Response {

    private static final long serialVersionUID = 1L;

    private GCRawMaterialSaveResponseBody body;
}
