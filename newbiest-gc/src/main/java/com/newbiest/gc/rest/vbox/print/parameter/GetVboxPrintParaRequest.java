package com.newbiest.gc.rest.vbox.print.parameter;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GetVboxPrintParaRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCGetVboxPrintParamter";

	private GetVboxPrintParaRequestBody body;

}
