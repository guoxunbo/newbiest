package com.newbiest.gc.rest.validation;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ValidationRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ValidationSoOrReTest";

	private ValidationRequestBody body;

}
