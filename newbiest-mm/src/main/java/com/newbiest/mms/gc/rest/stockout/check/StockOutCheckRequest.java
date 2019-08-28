package com.newbiest.mms.gc.rest.stockout.check;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StockOutCheckRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCMaterialLotManage";

	/**
	 * 获取出货前检验内容
	 */
	public static final String ACTION_GET_CHECK_LIST = "GetCheckList";

	/**
	 * 判定
	 */
	public static final String ACTION_JUDGE = "Judge";

	private StockOutCheckRequestBody body;

}
