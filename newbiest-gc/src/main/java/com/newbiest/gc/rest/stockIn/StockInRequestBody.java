package com.newbiest.gc.rest.stockIn;

import com.newbiest.gc.model.StockInModel;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class StockInRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Query/StockIn")
	private String actionType;

	@ApiModelProperty(value = "物料批次号")
	private String materialLotId;

	@ApiModelProperty(value = "载具aliasId")
	private String lotId;

	@ApiModelProperty(value = "直接绑定中转箱号，以及库位号")
	private List<StockInModel> stockInModels;

	@ApiModelProperty(value="动态表主键")
	private Long tableRrn;

}
