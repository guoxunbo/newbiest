package com.newbiest.mms.rest.materiallot;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotManage";

	public static final String ACTION_RECEIVE_2_WAREHOUSE = "Receive2Warehouse";
	public static final String ACTION_HOLD = "Hold";
	public static final String ACTION_RELEASE = "Release";
	public static final String ACTION_CONSUME = "Consume";

	private MaterialLotRequestBody body;

}
