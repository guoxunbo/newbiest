package com.newbiest.vanchip.rest.rawmaterial;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class RawMaterialResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private RawMaterialResponseBody body;
	
}
