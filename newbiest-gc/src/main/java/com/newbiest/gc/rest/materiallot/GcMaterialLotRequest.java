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
	 * 出货检
	 */
	public static final String ACTION_JUDGE_PACKED_LOT = "JudgePackedLot";

	/**
	 * 装箱检验
	 */
	public static final String ACTION_GET_PACK_CASE_CHECK_LIST = "GetPackCaseCheckList";

	/**
	 * WLT装箱检验
	 */
	public static final String ACTION_GET_WLT_PACK_CASE_CHECK_LIST = "GetWltPackCaseCheckList";

	/**
	 * 获取物料批次信息
	 */
	public static final String ACTION_QUERY_MATERIALLOT = "QueryMLot";

	/**
	 * 取消检验
	 */
	public static final String ACTION_CANCEL_CHECK = "CancelCheck";

	public static final String ACTION_QUERY_MATERIALLOTID_OR_LOTID = "QueryMaterialLotIdOrLotId";

	public static final String ACTION_QUERY_DATA = "QueryData";

	private GcMaterialLotRequestBody body;

}
