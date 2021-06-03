package com.newbiest.mms.rest.doc.issue.create;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CreateIssueOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "CreateIssueOrder";

	@ApiModelProperty("创建主材发料单")
	public static final String ACTION_TYPE_CREATE_ISSUE_LOT_ORDER = "CreateIssueMLotOrder";

	@ApiModelProperty("创建辅材发料单")
	public static final String ACTION_TYPE_CREATE_ISSUE_MATERIAL_ORDER = "CreateIssueMaterialOrder";

	@ApiModelProperty("创建成品发料单")
	public static final String ACTION_TYPE_CREATE_ISSUE_FINISH_GOOD_ORDER = "CreateIssueFinishGoodOrder";

	@ApiModelProperty("创建指定物料和数量发料单")
	public static final String ACTION_TYPE_CREATE_ISSUE_ORDER_BY_MATERIAL = "CreateIssueOrderByMaterial";

	@ApiModelProperty("创建指定批次和数量发料单")
	public static final String ACTION_TYPE_CREATE_ISSUE_ORDER_BY_MLOT = "CreateIssueOrderByMLot";

	private CreateIssueOrderRequestBody body;

}
