package com.newbiest.mms.rest.pack.validation;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ValidationPackMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ValidationPackMaterialLotResponseBody body;
	
}
