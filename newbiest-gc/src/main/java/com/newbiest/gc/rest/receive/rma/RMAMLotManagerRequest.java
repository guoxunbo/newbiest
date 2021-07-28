package com.newbiest.gc.rest.receive.rma;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class RMAMLotManagerRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCRMAMLotManager";

	public static final String ACTION_TYPE_RECEIVE = "Receive";

	public static final String ACTION_TYPE_PRINT = "Print";

	private RMAMLotManagerRequestBody body;

}
