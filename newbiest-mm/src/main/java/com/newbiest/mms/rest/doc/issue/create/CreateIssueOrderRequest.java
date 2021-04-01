package com.newbiest.mms.rest.doc.issue.create;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class CreateIssueOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "CreateIssueOrder";

	public static final String ACTION_TYPE_CREATE_ISSUE_LOT_ORDER = "CreateIssueMLotOrder";
	public static final String ACTION_TYPE_CREATE_ISSUE_MATERIAL_ORDER = "CreateIssueMaterialOrder";
	public static final String ACTION_TYPE_CREATE_ISSUE_MLOT_ORDER = "CreateIssueMaterialLotOrder";
	public static final String ACTION_TYPE_CREATE_ISSUE_FINISH_GOOD_ORDER = "CreateIssueFinishGoodOrder";

	private CreateIssueOrderRequestBody body;

}
