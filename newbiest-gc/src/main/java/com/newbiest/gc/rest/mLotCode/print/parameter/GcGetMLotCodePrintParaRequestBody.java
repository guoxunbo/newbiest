package com.newbiest.gc.rest.mLotCode.print.parameter;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class GcGetMLotCodePrintParaRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "物料批次信息")
	private List<MaterialLot> materialLotList;

	@ApiModelProperty(value = "标签类型")
	private String printType;
}
