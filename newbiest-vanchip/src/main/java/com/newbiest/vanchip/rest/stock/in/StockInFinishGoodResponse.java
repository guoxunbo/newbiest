package com.newbiest.vanchip.rest.stock.in;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class StockInFinishGoodResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private StockInFinishGoodResponseBody body;
	
}
