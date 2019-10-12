package com.newbiest.gc.rest.validationMaterial;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ValidationMaterialRequest extends Request {

    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_NAME = "ValidationMatrial";

    private ValidationMaterialRequestBody body;
}
