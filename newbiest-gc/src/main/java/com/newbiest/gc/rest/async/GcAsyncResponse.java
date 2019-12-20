package com.newbiest.gc.rest.async;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class GcAsyncResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcAsyncResponseBody body;
	
}
