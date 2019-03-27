package com.newbiest.mms.rest.materiallot.inv;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotInvRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotInvManage";

	public static final String ACTION_STOCK_IN = "StockIn";
	public static final String ACTION_STOCK_OUT = "StockOut";
	public static final String ACTION_TRANSFER = "Transfer";
	public static final String ACTION_PICK = "Pick";
	public static final String ACTION_CHECK = "Check";

	private MaterialLotInvRequestBody body;

}
