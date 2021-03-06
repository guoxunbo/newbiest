package com.newbiest.gc.rest.stockout;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StockOutRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCStockOut";

	private StockOutRequestBody body;

}
