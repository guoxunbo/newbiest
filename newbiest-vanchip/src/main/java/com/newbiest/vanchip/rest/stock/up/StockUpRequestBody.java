package com.newbiest.vanchip.rest.stock.up;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class StockUpRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "子单据号")
	private String docLineId;

	@ApiModelProperty(value = "物料批次")
	private List<MaterialLot> materialLots;

	@ApiModelProperty(value = "物料批次动作")
	private List<MaterialLotAction> materialLotActionList;



}
