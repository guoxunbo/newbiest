package com.newbiest.gc.rest.wltcp.print.parameter;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class PrintWltCpLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private PrintWltCpLotResponseBody body;
	
}
