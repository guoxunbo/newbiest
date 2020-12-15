package com.newbiest.gc.rest.cobBox.print.parameter;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcGetCobboxPrintParaResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcGetCobboxPrintParaResponseBody body;
	
}
