package com.newbiest.vanchip.rest.doc.check;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CheckMLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "CheckMLotManager";

	public static final String ACTION_TYPE_GET_RESERVED_MLOT = "GetReservedMLot";

	@ApiModelProperty("盘点")
	public static final String ACTION_TYPE_CHECK = "Check";

	private CheckMLotRequestBody body;

}
