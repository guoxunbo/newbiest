package com.newbiest.gc.scm.send.mlot.state;

import com.newbiest.msg.Request;
import lombok.Data;

@Data
public class MaterialLotStateReportRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MLotStateReport";

	private MaterialLotStateReportRequestBody body;

}
