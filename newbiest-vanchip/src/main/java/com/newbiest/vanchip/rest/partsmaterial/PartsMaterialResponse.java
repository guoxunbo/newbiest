package com.newbiest.vanchip.rest.partsmaterial;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class PartsMaterialResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private PartsMaterialResponseBody body;
	
}
