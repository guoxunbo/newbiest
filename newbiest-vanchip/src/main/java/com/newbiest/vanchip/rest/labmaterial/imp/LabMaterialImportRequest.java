package com.newbiest.vanchip.rest.labmaterial.imp;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class LabMaterialImportRequest extends Request {

    public static final String MESSAGE_NAME = "LabMaterialManager";

    private LabMaterialImportRequestBody body;
}
