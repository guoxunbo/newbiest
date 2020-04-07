package com.newbiest.rms.rest.eqp;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.rms.dto.EquipmentAction;
import com.newbiest.rms.model.Equipment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EquipmentRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "GetRecipe/Hold/Release等")
	private String actionType;

	@ApiModelProperty(value="设备相关信息")
	private EquipmentAction equipmentAction;

}
