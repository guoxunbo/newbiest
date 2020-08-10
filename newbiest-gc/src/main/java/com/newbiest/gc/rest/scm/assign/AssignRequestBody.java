package com.newbiest.gc.rest.scm.assign;

import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("具体请求操作信息")
public class AssignRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "动作类型", example = "Assign/UnAssign")
	private String actionType;

	@ApiModelProperty(value = "批次号")
	private String lotId;

	@ApiModelProperty(value = "晶圆号", example = "01")
	private String waferId;

}
