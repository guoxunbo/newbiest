package com.newbiest.vanchip.rest.IncomingMaterialImportManager.IncomingMaterialImportSave;

import com.newbiest.base.msg.ResponseBody;
import lombok.Data;

@Data
public class IncomingMaterialImportSaveResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private String importCode;
}
