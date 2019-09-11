package com.newbiest.gc.rest.check;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class CheckResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private CheckResponseBody body;
	
}
