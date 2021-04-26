package com.newbiest.vanchip.rest.doc.issue.create.laboratory;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CreateIssueOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "CreateIssueOrder";

	@ApiModelProperty("实验室发料,指定物料而非物料批次")
	public static final String ACTION_TYPE_CREATE_ISSUE_LABMLOT_ORDER = "createIssueLabMLotOrder";

	private CreateIssueOrderRequestBody body;

}
