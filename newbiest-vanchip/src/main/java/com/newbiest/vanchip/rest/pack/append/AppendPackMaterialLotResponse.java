package com.newbiest.vanchip.rest.pack.append;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class AppendPackMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private AppendPackMaterialLotResponseBody body;
	
}
