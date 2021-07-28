package com.newbiest.gc.rest.wltcp.print.parameter;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class PrintWltCpLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCGetWltOrCpPrintParamter";

	private PrintWltCpLotRequestBody body;

}
