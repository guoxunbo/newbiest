package com.newbiest.gc.rest.receive.ft;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class FTMLotManagerRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCFtMLotManager";

	public static final String ACTION_TYPE_RECEIVE = "Receive";

	public static final String ACTION_TYPE_QUERY = "Query";

	public static final String ACTION_TYPE_STOCK_IN = "StockIn";

	private FTMLotManagerRequestBody body;

}
