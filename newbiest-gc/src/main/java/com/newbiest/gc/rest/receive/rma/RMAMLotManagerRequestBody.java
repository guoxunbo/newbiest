package com.newbiest.gc.rest.receive.rma;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class RMAMLotManagerRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	@ApiModelProperty(value = "待处理的物料批次")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "打印标签")
	private String printLabel;

	@ApiModelProperty(value = "物料批次")
	private List<MaterialLot> materialLots;

	@ApiModelProperty(value = "打印份数")
	private String printCount;
}
