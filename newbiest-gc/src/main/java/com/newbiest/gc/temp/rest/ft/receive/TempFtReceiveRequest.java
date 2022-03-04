package com.newbiest.gc.temp.rest.ft.receive;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class TempFtReceiveRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "TempFtReceive";
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String ACTION_TYPE_RECEIVE = "receive";

	private TempFtReceiveRequestBody body;

}
