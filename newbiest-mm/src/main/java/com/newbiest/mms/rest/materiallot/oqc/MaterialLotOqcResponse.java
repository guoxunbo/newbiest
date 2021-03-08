package com.newbiest.mms.rest.materiallot.oqc;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class MaterialLotOqcResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private MaterialLotOqcResponseBody body;
	
}
