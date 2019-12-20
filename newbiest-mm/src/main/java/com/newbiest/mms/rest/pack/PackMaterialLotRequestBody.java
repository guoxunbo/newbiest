package com.newbiest.mms.rest.pack;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PackMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "物料批次操作")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "包装规则")
		private String packageType;

}
