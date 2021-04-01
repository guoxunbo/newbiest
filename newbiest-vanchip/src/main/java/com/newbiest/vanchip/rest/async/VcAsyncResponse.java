package com.newbiest.vanchip.rest.async;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class VcAsyncResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private VcAsyncResponseBody body;
	
}
