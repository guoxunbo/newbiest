package com.newbiest.gc.rest.materiallot;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcMaterialLotResponseBody body;
	
}
