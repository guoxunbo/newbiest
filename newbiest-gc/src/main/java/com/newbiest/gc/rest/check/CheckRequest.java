package com.newbiest.gc.rest.check;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class CheckRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCCheckInventory";

	public static final String ACTION_QUERY  = "Query";
	public static final String ACTION_CHECK  = "Check";

	private CheckRequestBody body;

}
