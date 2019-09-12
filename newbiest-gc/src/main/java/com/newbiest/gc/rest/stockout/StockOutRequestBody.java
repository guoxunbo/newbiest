package com.newbiest.gc.rest.stockout;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class StockOutRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "待重测发料的物料批次")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "单据详情")
	private DocumentLine documentLine;

}
