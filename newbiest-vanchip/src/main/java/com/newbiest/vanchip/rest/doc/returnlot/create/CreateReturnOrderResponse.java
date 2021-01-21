package com.newbiest.vanchip.rest.doc.returnlot.create;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class CreateReturnOrderResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private CreateReturnOrderResponseBody body;
	
}
