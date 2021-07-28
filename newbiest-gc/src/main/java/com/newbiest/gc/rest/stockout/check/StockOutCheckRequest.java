package com.newbiest.gc.rest.stockout.check;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StockOutCheckRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "StockOutCheck";

	/**
	 * 获取出货前检验内容
	 */
	public static final String ACTION_GET_CHECK_LIST = "GetCheckList";

	/**
	 * 获取出货前检验内容
	 */
	public static final String ACTION_GET_WLTCHECK_LIST = "GetWltCheckList";

	/**
	 * 判定
	 */
	public static final String ACTION_JUDGE = "Judge";

	/**
	 * 获取检验的物料批次
	 */
	public static final String ACTION_GET_CHECK_MLOT = "GetCheckMLot";

	private StockOutCheckRequestBody body;

}
