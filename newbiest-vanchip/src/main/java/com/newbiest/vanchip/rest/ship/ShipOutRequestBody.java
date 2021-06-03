package com.newbiest.vanchip.rest.ship;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class ShipOutRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "发货单")
	private DocumentLine documentLine;

	@ApiModelProperty(value = "物料批次号")
	private List<MaterialLot> materialLots;

	@ApiModelProperty(value = "发货单号")
	private String docLineId;



}
