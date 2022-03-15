package com.newbiest.gc.rest.IRAPackage;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class IRAPackageResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private IRAPackageResponseBody body;
	
}
