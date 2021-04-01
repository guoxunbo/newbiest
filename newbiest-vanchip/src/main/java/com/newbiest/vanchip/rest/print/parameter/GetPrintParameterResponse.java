package com.newbiest.vanchip.rest.print.parameter;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class GetPrintParameterResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GetPrintParameterResponseBody body;
	
}
