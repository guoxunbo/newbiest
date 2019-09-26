package com.newbiest.gc.rest.box.print.parameter;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcGetBboxPrintParaResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcGetBboxPrintParaResponseBody body;
	
}
