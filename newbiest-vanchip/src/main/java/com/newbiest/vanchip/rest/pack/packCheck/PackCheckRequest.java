package com.newbiest.vanchip.rest.pack.packCheck;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class PackCheckRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "PackCheck";

	public static final String ACTION_TYPE_PACK_CHECK = "PackCheck";

	private PackCheckRequestBody body;

}
