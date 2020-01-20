package com.newbiest.gc.rest.async;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcAsyncRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "AsyncManager";

	public static final String ACTION_ASYNC_RETEST_ISSUE_ORDER = "AsyncReTestIssueOrder";
	public static final String ACTION_ASYNC_WAFER_ISSUE_ORDER = "AsyncWaferIssueOrder";

	public static final String ACTION_ASYNC_RECEIVE_ORDER = "AsyncReceiveOrder";
	public static final String ACTION_ASYNC_SHIP_ORDER = "AsyncShipOrder";

	public static final String ACTION_ASYNC_MATERIAL = "AsyncMaterial";
	public static final String ACTION_ASYNC_PRODUCT = "AsyncProduct";

	private GcAsyncRequestBody body;

}
