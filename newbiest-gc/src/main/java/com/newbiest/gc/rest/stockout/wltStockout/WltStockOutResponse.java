package com.newbiest.gc.rest.stockout.wltStockout;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class WltStockOutResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private WltStockOutResponseBody body;
	
}
