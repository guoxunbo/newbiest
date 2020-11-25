package com.newbiest.gc.rest.hongkong.warehouse.manager;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class HKWarehouseRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "HKWarehouseManager";

	public static final String ACTION_QUERY_MLOT = "GetHKWarehouseMLot";

	public static final String ACTION_VALIDATE_HK_MLOT = "ValidateHKMlot";

	public static final String ACTION_HK_STOCK_OUT = "HKStockOut";

	public static final String ACTION_HK_BYORDER_STOCK_OUT = "HKByOrderStockOut";

	private HKWarehouseRequestBody body;

}
