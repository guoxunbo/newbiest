package com.newbiest.gc.rest.materiallot;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcMaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCMaterialLotManage";

	/**
	 * 绑定中转箱
	 */
	public static final String ACTION_BIND_RELAY_BOX = "BindRelayBox";
	public static final String ACTION_UNBIND_RELAY_BOX = "UnbindRelayBox";

	/**
	 * 备货
	 */
	public static final String ACTION_STORING_LOT = "StoringLot";

	/**
	 * 检验
	 */
	public static final String ACTION_JUDGE_PACKED_LOT = "JudgePackedLot";

	private GcMaterialLotRequestBody body;

}
