package com.newbiest.vanchip.rest.mobile;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MobileRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "PackMaterialLot";

	public static final String ACTION_STOCK_IN = "stockIn";

	public static final String ACTION_VALIDATE_STOCK_IN_BY_ORDER = "validateStockInByOrder";

	/**
	 * by单据来料入库
	 */
	public static final String ACTION_STOCK_IN_BY_ORDER = "stockInByOrder";

	public static final String ACTION_STOCK_OUT = "stockOut";
	public static final String ACTION_STOCK_OUT_BY_ORDER = "stockOutByOrder";

	public static final String ACTION_QUERY_PACKAGE_MLOT = "queryPackageMLot";
	public static final String ACTION_PACKAGE_MLOT = "packageMLot";

	public static final String ACTION_QUERY_SHIP_MLOT_BY_DOC = "queryShipMLotByDoc";
	public static final String ACTION_SHIP_MLOT = "shipMLot";

	public static final String ACTION_CHECK_MLOT_INVENTORY = "checkMLotInventory";

	public static final String ACTION_TRANSFER_INVENTORY = "transferInv";
	public static final String ACTION_STOCK_IN_FINISH_GOOD = "stockInFinishGood";

	public static final String ACTION_VAILADATE_TARGET_WAREHOUSE = "vailadateTargetWarehouse";
	public static final String ACTION_VAILADATE_FROM_WAREHOUSE = "vailadateFromWarehouse";

	/**
	 * 转库 支持批量操作
	 */
	public static final String ACTION_TRANSFER_INVENTORY_MLOTS = "transferInvMLots";

	private MobileRequestBody body;

}
