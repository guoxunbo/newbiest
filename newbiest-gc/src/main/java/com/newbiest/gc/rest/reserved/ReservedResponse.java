package com.newbiest.gc.rest.reserved;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class ReservedResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ReservedResponseBody body;
	
}
