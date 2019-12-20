package com.newbiest.mms.rest.pack.validation;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ValidationPackMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "ValidationPack/ValidationAppend")
	private String actionType;

	@ApiModelProperty(value = "包装类型")
	private String packageType;

	@ApiModelProperty(value = "已包装的物料批次号")
	private String packagedMaterialLotId;

	@ApiModelProperty(value = "等待被包装的批次")
	private List<MaterialLot> waitToPackMaterialLots;

}
