package com.newbiest.mms.gc.rest.stockout.check;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class StockOutCheckResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private StockOutCheckResponseBody body;
	
}
