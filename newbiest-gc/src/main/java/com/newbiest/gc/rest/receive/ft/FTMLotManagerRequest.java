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

	public static final String ACTION_TYPE_QUERY_WAIT_ISSUE_UNIT = "QueryWaitIssueUnit";

	public static final String ACTION_TYPE_FT_ISSUE = "FtIssue";

	public static final String ACTION_TYPE_FT_STOCK_OUT = "FTStockOut";

	public static final String ACTION_TYPE_FT_OUTORDER_ISSUE = "FTOutOrderIssue";

	public static final String ACTION_TYPE_SALE_SHIP = "SaleShip";

	public static final String ACTION_TYPE_BSW_FT_STOCK_OUT = "BSWFTStockOut";

	public static final String ACTION_TYPE_BSW_SALE_SHIP = "BSWSaleShip";

	private FTMLotManagerRequestBody body;

}
