package com.newbiest.gc.rest.unConfirmWaferSet;

import com.newbiest.gc.model.GcUnConfirmWaferSet;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guozhangLuo on 2020/12/11
 */
@Data
@ApiModel("具体请求操作信息")
public class UnConfirmWaferSetRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Create/Update等")
	private String actionType;

	@ApiModelProperty(value = "晶圆追踪配置信息")
	private GcUnConfirmWaferSet unConfirmWaferSet;

}
