package com.newbiest.vanchip.rest.retry;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class RetryInterfaceResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private RetryInterfaceResponseBody body;
	
}
