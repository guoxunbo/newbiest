package com.newbiest.gc.rest.rawMlot.print.parameter;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcGetRawMlotPrintParaRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCGetRawMlotPrintParamter";

	public static final String ACTION_RAWPRINT= "RawPrint";
	public static final String ACTION_IRABOXPRINT= "IRABoxPrint";

	private GcGetRawMlotPrintParaRequestBody body;

}
