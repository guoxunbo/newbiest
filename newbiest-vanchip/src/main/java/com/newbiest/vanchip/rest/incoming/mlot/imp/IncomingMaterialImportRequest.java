package com.newbiest.vanchip.rest.incoming.mlot.imp;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialImportRequest extends Request {


    public static final String MESSAGE_NAME = "IncomingMaterialImportManager";

    private IncomingMaterialImportRequestBody body;
}
