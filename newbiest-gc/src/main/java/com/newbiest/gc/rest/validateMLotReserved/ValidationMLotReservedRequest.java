package com.newbiest.gc.rest.validateMLotReserved;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ValidationMLotReservedRequest  extends Request {

    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_NAME = "ValidationMLotReserved";

    private ValidationMLotReservedRequestBody body;

}
