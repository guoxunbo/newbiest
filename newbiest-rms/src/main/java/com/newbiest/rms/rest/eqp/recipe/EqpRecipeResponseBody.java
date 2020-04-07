package com.newbiest.rms.rest.eqp.recipe;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.rms.model.Equipment;
import lombok.Data;

@Data
public class EqpRecipeResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Equipment equipment;

}
