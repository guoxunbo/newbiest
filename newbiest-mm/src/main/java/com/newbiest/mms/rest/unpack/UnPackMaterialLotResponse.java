package com.newbiest.mms.rest.unpack;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class UnPackMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private UnPackMaterialLotResponseBody body;
	
}
