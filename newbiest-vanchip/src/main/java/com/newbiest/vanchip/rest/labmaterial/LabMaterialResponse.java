package com.newbiest.vanchip.rest.labmaterial;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class LabMaterialResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private LabMaterialResponseBody body;
	
}
