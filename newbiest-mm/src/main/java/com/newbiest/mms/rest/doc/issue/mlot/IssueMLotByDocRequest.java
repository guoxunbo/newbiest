package com.newbiest.mms.rest.doc.issue.mlot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class IssueMLotByDocRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IssueMLotByDoc";

	@ApiModelProperty("指定物料批次发料")
	public static final String ACTION_TYPE_ISSUE_MLOT_BY_ORDER = "IssueMLotByOrder";

	private IssueMLotByDocRequestBody body;

}
