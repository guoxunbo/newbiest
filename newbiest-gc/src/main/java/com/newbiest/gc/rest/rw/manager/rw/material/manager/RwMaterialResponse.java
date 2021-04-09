package com.newbiest.gc.rest.rw.manager.rw.material.manager;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class RwMaterialResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private RwMaterialResponseBody body;
	
}
