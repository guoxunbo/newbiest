package com.newbiest.vanchip.rest.IncomingMaterialImportManager.IncomingMaterialImport;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialImportRequest extends Request {


    public static final String MESSAGE_NAME = "GCRawMaterialManager";

    public static final String NB_TABLE_NAME = "IncomingMaterialImport";

    private IncomingMaterialImportRequestBody body;
}
