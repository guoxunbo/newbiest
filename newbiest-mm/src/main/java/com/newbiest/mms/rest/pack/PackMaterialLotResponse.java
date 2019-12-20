package com.newbiest.mms.rest.pack;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class PackMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private PackMaterialLotResponseBody body;
	
}
