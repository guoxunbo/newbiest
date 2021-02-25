package com.newbiest.vanchip.rest.stock.in;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class StockInFinishGoodRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "物料批次")
	private List<MaterialLot> materialLots;

	@ApiModelProperty(value = "物料批次动作")
	private List<MaterialLotAction> materialLotActionList;



}
