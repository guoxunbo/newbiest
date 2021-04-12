package com.newbiest.vanchip.rest.mobile;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MobileRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "PackMaterialLot";

	public static final String ACTION_RECEIVE_MLOT = "receiveMLot";
	public static final String ACTION_STOCK_IN = "stockIn";
	public static final String ACTION_STOCK_OUT = "stockOut";

	public static final String ACTION_QUERY_PACKAGE_MLOT = "queryPackageMLot";
	public static final String ACTION_PACKAGE_MLOT = "packageMLot";

	public static final String ACTION_QUERY_SHIP_MLOT_BY_DOC = "queryShipMLotByDoc";
	public static final String ACTION_SHIP_MLOT = "shipMLot";

	public static final String ACTION_CHECK_MLOT_INVENTORY = "checkMLotInventory";
	public static final String ACTION_PRINT_MLOTS = "printMLots";

	private MobileRequestBody body;

}
