package com.newbiest.gc.rest.stockout;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class StockOutRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "单据详情")
	private DocumentLine documentLine;

	@ApiModelProperty(value = "单据信息")
	private List<DocumentLine> documentLineList;

	@ApiModelProperty(value = "箱信息")
	private MaterialLot queryMaterialLot;

	@ApiModelProperty(value = "待出货的批次")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "仓库")
	private String warehouseId;


}
