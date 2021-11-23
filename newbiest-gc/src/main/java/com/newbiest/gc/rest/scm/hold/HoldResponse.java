package com.newbiest.gc.rest.scm.hold;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class HoldResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private HoldResponseBody body;
	
}
