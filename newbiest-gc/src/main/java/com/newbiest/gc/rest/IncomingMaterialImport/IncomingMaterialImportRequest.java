package com.newbiest.gc.rest.IncomingMaterialImport;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialImportRequest  extends Request {

    public static final String MESSAGE_NAME = "GCIncomingImport";

    private IncomingMaterialImportRequestBody body;
}
