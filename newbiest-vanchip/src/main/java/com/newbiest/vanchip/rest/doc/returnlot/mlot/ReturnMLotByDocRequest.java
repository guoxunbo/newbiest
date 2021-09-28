package com.newbiest.vanchip.rest.doc.returnlot.mlot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ReturnMLotByDocRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ReturnMLotByDoc";

	@ApiModelProperty(value = "产线退料，退到仓库")
	public static final String ACTION_RETURN_MLOT = "ReturnMLot";

	public static final String ACTION_GET_RESERVED_MLOT = "GetReservedMLot";

	@ApiModelProperty(value = "仓库退料，退供应商/ERP")
	public static final String ACTION_TYPE_RETURN_MATERIAL_LOT = "ReturnMaterialLot";

	@ApiModelProperty(value = "获取已经备货得批次")
	public static final String ACTION_GET_STOCK_UP_MLOT = "GetStockUpMLot";

	private ReturnMLotByDocRequestBody body;

}
