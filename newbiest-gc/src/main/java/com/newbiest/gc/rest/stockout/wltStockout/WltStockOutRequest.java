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

	private WltStockOutRequestBody body;

}
