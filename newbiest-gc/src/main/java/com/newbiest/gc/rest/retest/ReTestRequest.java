package com.newbiest.gc.rest.retest;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ReTestRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCReTest";

	public static final String ACTION_MOBILE_RETEST = "MobileRetest";
	public static final String ACTION_COM_RETEST = "ComRetest";
	public static final String ACTION_FT_RETEST = "FtRetest";

	private ReTestRequestBody body;

}
