package com.newbiest.gc.rest.wltBox.print.parameter.wltLot.print.parameter;

import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class GcGetWltBoxPrintParaRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "来料晶圆信息")
	private List<MaterialLotUnit> materialLotUnitList;

	@ApiModelProperty(value = "操作类型")
	private String actionType;
}
