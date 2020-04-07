package com.newbiest.rms.rest.eqp;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class EquipmentRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "EquipmentManager";

	public static final String ACTION_GET_RECIPE_LIST = "GetRecipeList";
	public static final String ACTION_UPLOAD_RECIPE_BY_EAP = "UploadRecipeByEap";

	private EquipmentRequestBody body;

}
