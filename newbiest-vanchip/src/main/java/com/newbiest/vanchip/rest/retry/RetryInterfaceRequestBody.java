package com.newbiest.vanchip.rest.retry;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.InterfaceFail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class RetryInterfaceRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	private List<InterfaceFail> interfaceFailList;
}
