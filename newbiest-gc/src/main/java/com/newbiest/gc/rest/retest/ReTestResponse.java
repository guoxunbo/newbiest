package com.newbiest.gc.rest.retest;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ReTestResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private RetestResponseBody body;
	
}
