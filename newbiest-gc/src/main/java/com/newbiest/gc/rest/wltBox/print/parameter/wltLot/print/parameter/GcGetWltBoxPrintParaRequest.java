package com.newbiest.gc.rest.wltBox.print.parameter.wltLot.print.parameter;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcGetWltBoxPrintParaRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCGetWltBoxPrintParamter";

	public static final String ACTION_TYPE_WLT_BBOX_LABEL = "WltBboxLabel";

	public static final String ACTION_TYPE_WAFER_LABEL = "WaferLabel";

	private GcGetWltBoxPrintParaRequestBody body;

}
