package com.newbiest.gc.rest.validation;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ValidationResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ValidationResponseBody body;
	
}
