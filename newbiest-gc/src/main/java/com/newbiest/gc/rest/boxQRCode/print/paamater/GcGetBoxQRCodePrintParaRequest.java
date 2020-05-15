package com.newbiest.gc.rest.boxQRCode.print.paamater;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcGetBoxQRCodePrintParaRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCGetBoxQRCodePrintParamter";

	public static final String ACTION_COB_PRINT_LABEL = "PrintCOBLabel";
	public static final String ACTION_PRINT_QRCODE_LABEL = "PrintQRCodeLabel";

	private GcGetBoxQRCodePrintParaRequestBody body;

}
