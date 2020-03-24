package com.newbiest.mms.rest.materiallot.unit;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class MaterialLotUnitResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private MaterialLotUnitResponseBody body;

	private String message;
	
}
