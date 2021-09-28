package com.newbiest.vanchip.rest.stock.up;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class StockUpResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private StockUpResponseBody body;
	
}
