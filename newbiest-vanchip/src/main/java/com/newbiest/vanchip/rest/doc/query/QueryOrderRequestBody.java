package com.newbiest.vanchip.rest.doc.query;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class QueryOrderRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("操作类型")
	private String actionType;

	@ApiModelProperty("单据号")
	private String documentId;

	@ApiModelProperty("物料批次号")
	private String materialLotId;

	@ApiModelProperty("单据类型")
	private String documentCategory;

}
