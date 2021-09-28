package com.newbiest.vanchip.rest.retry;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class RetryInterfaceRequest extends Request {

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE_NAME = "RetryInterfaceManager";

	public static final String ACTION_RETRY_INTERFACE = "retryInterface";

	private RetryInterfaceRequestBody body;

}
