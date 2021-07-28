package com.newbiest.gc.rest.rw.manager;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class RwMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private RwMaterialLotResponseBody body;
	
}
