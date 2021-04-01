package com.newbiest.vanchip.rest.async;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("具体请求操作信息")
public class VcAsyncRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "AsyncSo/AsyncMaterialOutOrder/AsyncMaterial/AsyncProduct")
	private String actionType;

}
