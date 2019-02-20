package com.newbiest.commom.sm.rest.statusmodel;

import com.newbiest.msg.Request;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class StatusModelRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "StatusModelManage";

	public static final String ACTION_DISPATCH_EVENT = "DispatchEvent";

	private StatusModelRequestBody body;

}
