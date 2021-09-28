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

	public static final String ACTION_STOCK_OUT_PARTS_MLOT = "StockOutPartsMLot";
	public static final String ACTION_STOCK_OUT_PARTS_MLOT_BY_ORDER = "StockOutPartsMLotByOrder";
	public static final String ACTION_RETURN_PARTS_WAREHOUSE = "ReturnPartsWarehouse";
	public static final String ACTION_CREATE_PARTS_WAREHOUSE = "CreateParts2Warehouse";

	private MaterialLotInventoryRequestBody body;

}
