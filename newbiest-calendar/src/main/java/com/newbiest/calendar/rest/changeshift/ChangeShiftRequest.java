package com.newbiest.calendar.rest.changeshift;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ChangeShiftRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ChangeShiftManage";

	public static final String ACTION_CLOSE = "Close";
	public static final String ACTION_OPEN = "Open";

	private ChangeShiftRequestBody body;

}
