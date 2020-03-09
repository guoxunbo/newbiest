package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialSave;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialSaveRequest extends Request {

    public static final String MESSAGE_NAME = "GCIncomingImport";
    public static final String ACTION_SAVE = "Save";

    private IncomingMaterialSaveRequestBody body;
}
