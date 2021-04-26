package com.newbiest.vanchip.rest.doc.issue.labmlot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IssueLabMLotOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "LabMLotManage";

	public static final String ACTION_TYPE_RECOMMEND_ISSUE_ORDER = "recommendIssueOrder";
	public static final String ACTION_TYPE_ISSUE_LAB_MLOT_ORDER = "issueLabMLotOrder";

	private IssueLabMLotOrderRequestBody body;

}
