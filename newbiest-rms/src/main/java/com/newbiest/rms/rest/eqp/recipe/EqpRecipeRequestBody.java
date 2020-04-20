package com.newbiest.rms.rest.eqp.recipe;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.rms.model.RecipeEquipment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EqpRecipeRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Create/Active/UploadByEap等相关")
	private String actionType;

	@ApiModelProperty(value = "设备recipe")
	private RecipeEquipment recipeEquipment;

}
