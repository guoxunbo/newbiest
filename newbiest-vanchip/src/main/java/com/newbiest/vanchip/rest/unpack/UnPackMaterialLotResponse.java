package com.newbiest.vanchip.rest.unpack;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class UnPackMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private UnPackMaterialLotResponseBody body;
	
}
