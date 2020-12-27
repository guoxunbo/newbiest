package com.newbiest.vanchip.rest.IncomingMaterialImportManager.IncomingMaterialImportSave;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialImportSaveRequest extends Request {

    public static final String MESSAGE_NAME = "GCRawMaterialManager";

    private IncomingMaterialImportSaveRequestBody body;
}
