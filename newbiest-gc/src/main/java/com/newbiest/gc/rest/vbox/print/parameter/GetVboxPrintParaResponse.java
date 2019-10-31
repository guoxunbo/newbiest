package com.newbiest.gc.rest.vbox.print.parameter;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GetVboxPrintParaResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GetVboxPrintParaResponseBody body;
	
}
