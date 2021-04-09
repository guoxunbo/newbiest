package com.newbiest.gc.rest.rw.manager.rw.material.manager;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class RwMaterialRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCRwMaterialManager";

	public static final String ACTION_TAPE_SCAN = "TapeScan";

	public static final String ACTION_TAPE_RECEIVE = "TapeReceive";

	private RwMaterialRequestBody body;

}
