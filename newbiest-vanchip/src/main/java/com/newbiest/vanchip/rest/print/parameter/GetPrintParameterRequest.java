package com.newbiest.vanchip.rest.print.parameter;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class GetPrintParameterRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "PrintParameterManager";

	@ApiModelProperty(value = "箱标签打印")
	public static final String ACTION_BOX = "GetBoxParameter";

	@ApiModelProperty(value = "荣耀外箱标签打印参数")
	public static final String ACTION_RY_BOX = "GetRYBoxParameter";

	@ApiModelProperty(value = "配料单打印")
	public static final String ACTION_PKLIST = "GetPKListParameter";

	@ApiModelProperty(value = "发料单打印")
	public static final String ACTION_ISSUE_ORDER = "GetIssueOrderParameter";

	private GetPrintParameterRequestBody body;

}
