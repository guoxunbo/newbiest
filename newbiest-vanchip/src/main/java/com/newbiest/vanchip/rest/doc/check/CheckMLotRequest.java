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
	public static final String ACTION_TYPE_CHECK_MLOT = "CheckMLot";
	public static final String ACTION_TYPE_CHECK_MLOT_BY_ORDER = "CheckMLotByOrder";

	@ApiModelProperty("获取需复盘批次的动作")
	public static final String ACTION_TYPE_GET_RECHECK_MLOT = "GetRecheckMLot";
	@ApiModelProperty("复盘")
	public static final String ACTION_TYPE_RECHECK_MLOT_BY_ORDER = "RecheckMLotByOrder";

	@ApiModelProperty("将批次库存信息同步到ERP")
	public static final String ACTION_TYPE_SEND_MLOT_INV_BY_ERP = "SendMLotInvByERP";

	private CheckMLotRequestBody body;

}
