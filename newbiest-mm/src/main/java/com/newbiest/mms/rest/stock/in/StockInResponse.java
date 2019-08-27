package com.newbiest.mms.rest.stock.in;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class StockInResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private StockInResponseBody body;
	
}
