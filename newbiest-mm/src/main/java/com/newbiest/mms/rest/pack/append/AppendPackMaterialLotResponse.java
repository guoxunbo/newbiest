package com.newbiest.mms.rest.pack.append;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class AppendPackMaterialLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private AppendPackMaterialLotResponseBody body;
	
}
