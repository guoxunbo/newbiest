package com.newbiest.gc.rest.async;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcAsyncRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "AsyncManager";


	public static final String ACTION_ASYNC_SO = "AsyncSo";
	public static final String ACTION_ASYNC_MATERIAL_OUT_ORDER = "AsyncMaterialOutOrder";
	public static final String ACTION_ASYNC_MATERIAL = "AsyncMaterial";

	private GcAsyncRequestBody body;

}
