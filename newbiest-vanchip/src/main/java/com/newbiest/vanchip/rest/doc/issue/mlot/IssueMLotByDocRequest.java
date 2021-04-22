package com.newbiest.vanchip.rest.doc.issue.mlot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class IssueMLotByDocRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IssueMLotByDoc";

	public static final String ACTION_TYPE_ISSUE = "Issue";

	@ApiModelProperty("对指定物料批次的发料单进行查询")
	public static final String ACTION_TYPE_GET_WAIT_ISSUE_MLOT_BY_ORDER_ID = "GetWaitIssueMLotByOrderId";

	@ApiModelProperty("对指定物料批次的发料单进行发料")
	public static final String ACTION_TYPE_ISSUE_MLOT_BY_DOC = "IssueMLotByDoc";

	private IssueMLotByDocRequestBody body;

}
