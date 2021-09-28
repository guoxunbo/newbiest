package com.newbiest.vanchip.rest.doc.create;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class CreateOrderResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private CreateOrderResponseBody body;
	
}
