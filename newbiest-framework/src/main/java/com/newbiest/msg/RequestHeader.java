package com.newbiest.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "请求的头信息", description = "所有请求都必须要携带")
public class RequestHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "消息名称")
	private String messageName;

	@ApiModelProperty(value = "请求携带的UUID确保唯一", required = true)
	private String transactionId;

	@ApiModelProperty(value = "区域主键")
	private Long orgRrn;

	@ApiModelProperty(value = "区域名称")
	private String orgName;

	@ApiModelProperty(value = "请求操作的用户名", required = true)
	private String username;

	@ApiModelProperty(value = "语言", required = true)
	private String language;

}
