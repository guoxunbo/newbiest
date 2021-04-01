package com.newbiest.vanchip.rest.stock.out;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class StockOutResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private StockOutResponseBody body;
	
}
