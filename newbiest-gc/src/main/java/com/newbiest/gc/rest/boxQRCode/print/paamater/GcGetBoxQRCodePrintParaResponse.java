package com.newbiest.gc.rest.boxQRCode.print.paamater;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcGetBoxQRCodePrintParaResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcGetBoxQRCodePrintParaResponseBody body;
	
}
