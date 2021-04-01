package com.newbiest.vanchip.rest.pack.packCheck;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class PackCheckResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private PackCheckResponseBody body;
	
}
