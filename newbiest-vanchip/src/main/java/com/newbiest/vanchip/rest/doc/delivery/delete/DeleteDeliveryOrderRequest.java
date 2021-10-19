package com.newbiest.vanchip.rest.doc.delivery.delete;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class DeleteDeliveryOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "DeleteDelivery";

	public static final String ACTION_TYPE_DELETE = "Delete";

	private DeleteDeliveryOrderRequestBody body;

}
