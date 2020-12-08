package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GCRawMaterialSaveRequest extends Request {

    public static final String MESSAGE_NAME = "GCRawMaterialManager";

    private GCRawMaterialSaveRequestBody body;
}
