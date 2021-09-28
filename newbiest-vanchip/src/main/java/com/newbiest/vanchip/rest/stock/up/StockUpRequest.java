package com.newbiest.vanchip.rest.stock.up;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StockUpRequest extends Request {

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE_NAME = "MaterialLotStockUp";

	public static final String ACTION_GET_MATERIAL_LOT= "GetMaterialLot";

	public static final String ACTION_SOTCK_UP_MLOT = "StockUp";

	private StockUpRequestBody body;

}
