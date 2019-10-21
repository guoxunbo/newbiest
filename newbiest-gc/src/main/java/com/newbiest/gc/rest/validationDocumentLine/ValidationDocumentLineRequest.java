package com.newbiest.gc.rest.validationDocumentLine;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ValidationDocumentLineRequest extends Request {

    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_NAME = "ValidationDocumentLine";

    private ValidationDocumentLineRequestBody body;
}
