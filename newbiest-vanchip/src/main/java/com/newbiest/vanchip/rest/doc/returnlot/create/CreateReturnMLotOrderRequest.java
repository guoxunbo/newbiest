package com.newbiest.vanchip.rest.doc.returnlot.create;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class CreateReturnMLotOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "CreateReturnMLotOrder";

	public static final String ACTION_TYPE_CREATE_RETURN_MLOT_ORDER = "CreateReturnMLotOrder";
	public static final String ACTION_TYPE_CREATE_RETURN_MATERIAL_ORDER = "CreateReturnMaterialOrder";

	private CreateReturnMLotOrderRequestBody body;

}
