package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class GCRawMaterialSaveResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private String importCode;
}
