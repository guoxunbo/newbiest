package com.newbiest.gc.rest.wltBox.print.parameter;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcGetWltBboxPrintParaResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcGetWltBboxPrintParaResponseBody body;
	
}
