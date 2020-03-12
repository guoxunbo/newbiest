package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialDelete;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialDeleteRequest extends Request {

    public static final String MESSAGE_NAME = "GCIncomingDelete";

    private IncomingMaterialDeleteRequestBody body;

}
