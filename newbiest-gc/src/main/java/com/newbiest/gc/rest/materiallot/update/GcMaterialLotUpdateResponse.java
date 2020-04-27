package com.newbiest.gc.rest.materiallot.update;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcMaterialLotUpdateResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcMaterialLotUpdateResponseBody body;
	
}
