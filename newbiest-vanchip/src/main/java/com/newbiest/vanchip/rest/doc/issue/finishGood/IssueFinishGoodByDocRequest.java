package com.newbiest.vanchip.rest.doc.issue.finishGood;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IssueFinishGoodByDocRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IssueFinishGoodManager";

	public static final String ACTION_TYPE_GET_MATERIAL_LOT = "GetMaterialLot";
	public static final String ACTION_TYPE_ISSUE_FINISH_GOOD = "IssueFinishGood";

	private IssueFinishGoodByDocRequestBody body;

}
