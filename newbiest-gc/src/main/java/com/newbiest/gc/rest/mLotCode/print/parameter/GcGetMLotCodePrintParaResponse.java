package com.newbiest.gc.rest.mLotCode.print.parameter;

import com.newbiest.msg.Response;
import lombok.Data;


@Data
public class GcGetMLotCodePrintParaResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private GcGetMLotCodePrintParaResponseBody body;
	
}
