package com.newbiest.rms.rest.eqp.recipe;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class EqpRecipeRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "EqpRecipeManager";

	private EqpRecipeRequestBody body;

}
