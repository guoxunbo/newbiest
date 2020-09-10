package com.newbiest.gc.rest.stockout.wltStockout;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class WltStockOutRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCWltStockOut";

	public static final String ACTION_WLTSTOCKOUT= "WltStockOut";

	public static final String ACTION_VALIDATION_WLTMLOT = "validationWltMlot";

	public static final String ACTION_QUERY_STOCKOUTTAG_MLOTUNIT = "queryTagMlotUnit";

	public static final String ACTION_STOCKOUTTAG = "StockOutTag";

	public static final String ACTION_UNSTOCKOUTTAG = "UnStockOutTag";

	public static final String ACTION_VALIDATE_VENDER = "ValidateVender";

	private WltStockOutRequestBody body;

}
