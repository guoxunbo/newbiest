package com.newbiest.gc.rest.stockout;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class StockOutResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private StockOutResponseBody body;
	
}
