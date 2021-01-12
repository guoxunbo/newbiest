package com.newbiest.vanchip.rest.incoming.mlot.delete;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialDeleteRequest extends Request {

    public static final String MESSAGE_NAME = "IncomingMaterialImportManager";

    private IncomingMaterialDeleteRequestBody body;


}