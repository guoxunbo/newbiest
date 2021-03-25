package com.newbiest.gc.scm.send.mlot.state;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class MaterialLotStateReportResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private MaterialLotStateReportResponseBody body;
	
}
