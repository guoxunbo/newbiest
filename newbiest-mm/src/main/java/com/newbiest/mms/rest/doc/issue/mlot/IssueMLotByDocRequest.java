package com.newbiest.mms.rest.doc.issue.mlot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IssueMLotByDocRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IssueMLotByDoc";

	public static final String ACTION_TYPE_ISSUE = "Issue";
	public static final String ACTION_TYPE_GET_MATERIAL_LOT = "GetMaterialLot";

	public static final String ACTION_TYPE_ISSUE_MLOT = "IssueMLot";
	public static final String ACTION_TYPE_GET_WAIT_ISSUE_MLOT = "GetWaitIssueMLotByDocId";

	private IssueMLotByDocRequestBody body;

}
