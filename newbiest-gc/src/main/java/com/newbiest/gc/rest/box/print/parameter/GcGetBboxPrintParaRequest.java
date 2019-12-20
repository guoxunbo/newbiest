package com.newbiest.gc.rest.box.print.parameter;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcGetBboxPrintParaRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCGetBboxPrintParamter";

	private GcGetBboxPrintParaRequestBody body;

}
