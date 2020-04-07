package com.newbiest.rms.rest.eqp.recipe;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class EqpRecipeResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private EqpRecipeResponseBody body;
	
}
