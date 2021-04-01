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

	@ApiModelProperty(value = "coc标签打印")
	public static final String ACTION_COC = "GetCocParameter";

	@ApiModelProperty(value = "出货清单打印")
	public static final String ACTION_SHIPPING_LIST = "GetShippingListParameter";
	public static final String ACTION_SHIPPING_LIST_MLOT = "GetShippingListMLotParameter";

	@ApiModelProperty(value = "装箱清单打印")
	public static final String ACTION_PACKING_LIST = "GetPackingListParameter";

	@ApiModelProperty(value = "箱标签打印")
	public static final String ACTION_BOX = "GetBoxParameter";

	@ApiModelProperty(value = "配料单打印")
	public static final String ACTION_PKLIST = "GetPKListParameter";

	@ApiModelProperty(value = "发料单打印")
	public static final String ACTION_ISSUE_ORDER = "GetIssueOrderParameter";

	private GetPrintParameterRequestBody body;

}
