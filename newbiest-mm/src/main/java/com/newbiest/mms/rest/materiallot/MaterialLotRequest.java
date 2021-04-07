package com.newbiest.mms.rest.materiallot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotManage";

	public static final String ACTION_RECEIVE_2_WAREHOUSE = "Receive2Warehouse";
	public static final String ACTION_CONSUME = "Consume";
	public static final String ACTION_PRINT_LABEL = "PrintLabel";

	private MaterialLotRequestBody body;

}
