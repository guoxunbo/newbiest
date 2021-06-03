package com.newbiest.vanchip.rest.pack.packCheck;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PackCheckRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("操作类型")
	private String actionType;

	@ApiModelProperty("判定动作")
	private MaterialLotAction materialLotAction;
}
