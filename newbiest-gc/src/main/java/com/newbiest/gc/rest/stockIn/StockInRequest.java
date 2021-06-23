package com.newbiest.gc.rest.stockIn;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StockInRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCStockIn";

	public static final String ACTION_QUERY = "Query";
	public static final String ACTION_QUERY_WAFER = "QueryWafer";
	public static final String ACTION_STOCK_IN = "StockIn";
	public static final String ACTION_QUERY_MATERIAL = "QueryMaterial";
	public static final String ACTION_QUERY_MATERIAL_INFO = "QueryMaterialInfo";

	private StockInRequestBody body;

}
