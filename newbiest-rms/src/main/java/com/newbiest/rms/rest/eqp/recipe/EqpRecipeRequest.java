package com.newbiest.rms.rest.eqp.recipe;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class EqpRecipeRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "EqpRecipeManager";

	public static final String ACTION_TYPE_SET_GOLDEN = "SetGolden";

	public static final String ACTION_TYPE_UNSET_GOLDEN = "UnSetGolden";

	private EqpRecipeRequestBody body;

}
