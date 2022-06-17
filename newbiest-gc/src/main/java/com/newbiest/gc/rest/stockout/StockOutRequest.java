package com.newbiest.gc.rest.stockout;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StockOutRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCStockOut";

	public static final String ACTION_STOCKOUT= "StockOut";

	public static final String ACTION_VALIDATION = "validation";

	public static final String ACTION_SALESHIP = "SaleShip";

	public static final String ACTION_TRANSFER_SHIP = "TransferShip";

	private StockOutRequestBody body;

}
