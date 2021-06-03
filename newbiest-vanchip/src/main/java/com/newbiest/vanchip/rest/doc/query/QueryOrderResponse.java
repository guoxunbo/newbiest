package com.newbiest.vanchip.rest.doc.query;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class QueryOrderResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private QueryOrderResponseBody body;
	
}
