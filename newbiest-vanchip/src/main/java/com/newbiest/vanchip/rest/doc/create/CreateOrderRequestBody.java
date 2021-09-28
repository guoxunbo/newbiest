package com.newbiest.vanchip.rest.doc.create;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.DocumentLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class CreateOrderRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("操作类型")
	private String actionType;

	@ApiModelProperty("单据信息")
	private Document document;

	@ApiModelProperty("单据详细信息")
	private DocumentLine documentLine;

}
