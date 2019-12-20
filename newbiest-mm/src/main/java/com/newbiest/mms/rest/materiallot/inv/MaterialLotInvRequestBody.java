package com.newbiest.mms.rest.materiallot.inv;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel("具体请求操作信息")
public class MaterialLotInvRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Receive/StockIn等")
	private String actionType;

	@ApiModelProperty(value = "物料批次")
	private MaterialLot materialLot;

	@ApiModelProperty("物料操作，包含了数量仓库等")
	private MaterialLotAction materialLotAction;

}
