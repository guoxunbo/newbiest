package com.newbiest.vanchip.rest.doc.scrap;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ScrapMLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ScrapMLotManager";

	public static final String ACTION_TYPE_GET_RESERVED_MLOT = "GetReservedMLot";

	public static final String ACTION_TYPE_SCRAP_MLOT_BY_ORDER = "ScrapMLotByOrder";

	@ApiModelProperty(value = "验证单据匹配规则")
	public static final String ACTION_VALIDATE_RESERVED_RULE = "ValidateReservedMLot";

	private ScrapMLotRequestBody body;

}
