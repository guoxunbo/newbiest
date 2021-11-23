package com.newbiest.gc.rest.rawMlot.print.parameter;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcGetRawMlotPrintParaResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcGetRawMlotPrintParaResponseBody body;
	
}
