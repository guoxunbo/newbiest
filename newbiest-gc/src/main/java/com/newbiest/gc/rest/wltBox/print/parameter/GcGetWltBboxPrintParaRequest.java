package com.newbiest.gc.rest.wltBox.print.parameter;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcGetWltBboxPrintParaRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCGetWltBboxPrintParamter";

	private GcGetWltBboxPrintParaRequestBody body;

}
