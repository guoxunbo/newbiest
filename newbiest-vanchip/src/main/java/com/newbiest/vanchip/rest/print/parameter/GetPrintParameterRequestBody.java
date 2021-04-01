package com.newbiest.vanchip.rest.print.parameter;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("具体请求操作信息")
public class GetPrintParameterRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "原料发料单号")
	private String documentId;

	@ApiModelProperty(value = "发货单号")
	private String documentLineId;

	@ApiModelProperty(value = "物料批次")
	private String materialLotId;

}
