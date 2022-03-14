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

	public static final String ACTION_WLTOTHERSTOCKOUT = "WltOtherStockOut";

	public static final String ACTION_HN_SAMPLE_COLLECTION_STOCK_OUT= "HNSampleCollectionStockOut";

	public static final String ACTION_VALIDATION_WLTMLOT = "validationWltMlot";

	public static final String ACTION_QUERY_STOCKOUTTAG_MLOTUNIT = "queryTagMlotUnit";

	public static final String ACTION_STOCKOUTTAG = "StockOutTag";

	public static final String ACTION_UNSTOCKOUTTAG = "UnStockOutTag";

	public static final String ACTION_VALIDATE_VENDER = "ValidateVender";

	public static final String ACTION_GETMLOT = "GetMLot";

	public static final String ACTION_VALIDATE_MATERIAL_NAME = "ValidateMaterialName";

	public static final String ACTION_THREESIDE_SHIP = "ThreeSideShip";

	public static final String ACTION_SALE_SHIP = "SaleShip";

	public static final String ACTION_GC_RW_ATTRIBUTE_CHANGE = "GCRWAttributeChange";

	public static final String ACTION_MOBILE_WLT_STOCK_OUT = "MobileWltStockOut";

	public static final String ACTION_MOBILE_SALE_SHIP = "MobileSaleShip";

	private WltStockOutRequestBody body;

}
