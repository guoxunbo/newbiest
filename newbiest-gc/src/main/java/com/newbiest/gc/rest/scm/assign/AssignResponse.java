package com.newbiest.gc.rest.scm.assign;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class AssignResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private AssignResponseBody body;
	
}
