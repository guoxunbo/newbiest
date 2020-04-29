package com.newbiest.gc.rest.receive.wafer;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class WaferManagerRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCWaferManager";

	public static final String ACTION_TYPE_RECEIVE = "Receive";
	public static final String ACTION_TYPE_VALIDATION_ISSUE = "ValidationIssue";
	public static final String ACTION_TYPE_ISSUE = "Issue";
	public static final String ACTION_TYPE_VALIDATION_WAIT_ISSUE = "ValidationWaitIssue";

	private WaferManagerRequestBody body;

}
