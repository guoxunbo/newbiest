package com.newbiest.gc.rest.wltcp.print.parameter;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("具体请求操作信息")
public class PrintWltCpLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作对象/物料信息")
	private MaterialLot materialLot;

	@ApiModelProperty(value = "操作类型", example = "getPrintParameter")
	private String actionType;

}
