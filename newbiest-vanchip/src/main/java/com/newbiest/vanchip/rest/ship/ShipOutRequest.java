package com.newbiest.vanchip.rest.ship;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ShipOutRequest extends Request {

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE_NAME = "StockOutManager";

	public static final String ACTION_TYPE_GET_MATERIAL_LOT = "GetMaterialLot";
	public static final String ACTION_TYPE_SHIP_OUT = "ShipOut";

	private ShipOutRequestBody body;

}
