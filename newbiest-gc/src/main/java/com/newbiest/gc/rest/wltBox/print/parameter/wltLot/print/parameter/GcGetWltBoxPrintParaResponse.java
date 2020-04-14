package com.newbiest.gc.rest.wltBox.print.parameter.wltLot.print.parameter;

import com.newbiest.gc.rest.wltBox.print.parameter.GcGetWltBboxPrintParaResponseBody;
import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcGetWltBoxPrintParaResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcGetWltBoxPrintParaResponseBody body;
	
}
