package com.newbiest.gc.rest.receive.ft;

import com.newbiest.gc.model.StockInModel;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class FTMLotManagerRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	@ApiModelProperty(value = "待处理的WaferID")
	private List<MaterialLotUnit> materialLotUnitList;

	@ApiModelProperty(value = "待处理的WaferID")
	private String unitId;

	@ApiModelProperty(value="动态表主键")
	private Long tableRrn;

	@ApiModelProperty(value = "直接绑定中转箱号，以及库位号")
	private List<StockInModel> stockInModels;


}
