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
	 */
	@Deprecated
	public static final String ACTION_TYPE_VALIDATION_WAIT_ISSUE = "GetWaitIssueMLot";

	/**
	 * 委外晶圆接收
	 */
	public static final String ACTION_TYPE_PURCHASEOUTSOURE_RECEIVE = "PurchaseOutsoureReceive";

	/**
	 * 香港仓接收
	 */
	public static final String ACTION_TYPE_HK_MLOT_RECEIVE = "HKMLotReceive";

	/**
	 * COG来料接收
	 */
	public static final String ACTION_TYPE_COG_MLOT_RECEIVE = "CogReceive";

	private WaferManagerRequestBody body;

}
