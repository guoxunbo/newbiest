package com.newbiest.vanchip.rest.async;


import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class VcAsyncRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "AsyncManager";
	public static final String ACTION_ASYNC_PRODUCT = "AsyncProduct";

	private VcAsyncRequestBody body;

}
