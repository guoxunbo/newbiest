package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialImport;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GCRawMaterialImportRequest extends Request {


    public static final String MESSAGE_NAME = "GCRawMaterialManager";

    public static final String NB_TABLE_NAME = "GCRawMaterialImport";

    private GCRawMaterialImportRequestBody body;
}
