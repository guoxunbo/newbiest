package com.newbiest.vanchip.rest.doc.create;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel
public class CreateOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "CreateOrderManager";

	@ApiParam("创建单据")
	public static final String ACTION_CREATE_DOCUMENT = "createDocument";

	@ApiParam("创建单据详情")
	public static final String ACTION_CREATE_DOCUMENT_LINE = "createDocumentLine";

	private CreateOrderRequestBody body;

}
