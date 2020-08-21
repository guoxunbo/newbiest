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

	/**
	 * 需要先绑定工单再发料
	 * 暂时不用
	 */
	@Deprecated
	public static final String ACTION_TYPE_VALIDATION_WAIT_ISSUE = "ValidationWaitIssue";

	/**
	 * 委外晶圆接收
	 */
	public static final String ACTION_TYPE_PURCHASEOUTSOURE_RECEIVE = "PurchaseOutsoureReceive";

	private WaferManagerRequestBody body;

}
