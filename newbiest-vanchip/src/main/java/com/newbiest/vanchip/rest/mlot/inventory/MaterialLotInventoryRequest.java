package com.newbiest.vanchip.rest.mlot.inventory;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotInventoryRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotInvManage";

	public static final String ACTION_PICKS = "Picks";
	public static final String ACTION_GET_STOCK_OUT_MLOT_BY_ORDER = "GetStockOutMLotByOrder";


	private MaterialLotInventoryRequestBody body;

}
