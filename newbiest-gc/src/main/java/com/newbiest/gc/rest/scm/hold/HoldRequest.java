package com.newbiest.gc.rest.scm.hold;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class HoldRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ScmHoldRelease";

	public static final String ACTION_TYPE_HOLD = "Hold";
	public static final String ACTION_TYPE_RELEASE = "Release";

	private HoldRequestBody body;

}
