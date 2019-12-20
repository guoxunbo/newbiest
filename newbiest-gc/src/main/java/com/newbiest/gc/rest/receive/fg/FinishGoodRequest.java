package com.newbiest.gc.rest.receive.fg;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class FinishGoodRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCFinishGoodManage";

	private FinishGoodRequestBody body;

}
