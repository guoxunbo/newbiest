package com.newbiest.gc.rest.mesSaveMLotHis;

import com.newbiest.gc.rest.materiallot.GcMaterialLotRequestBody;
import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MesSaveMLotHisRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "mesSaveMLotHisManager";

	/**
	 * 保存晶圆历史
	 */
	public static final String ACTION_SAVE_MLOTUNIT_HIS = "SaveMLotUnitHis";

	/**
	 * MES接收原材料
	 */
	public static final String ACTION_RECEIVE_RAW_MATERIAL = "RawMaterialReceive";

	/**
	 * IRA退料
	 */
	public static final String ACTION_IRA_RETURN = "IraReturnWarehouse";

	/**
	 * 金线退仓库
	 */
	public static final String ACTION_WIRE_RETURN = "WireReturnWarehouse";

	/**
	 * 胶水退仓库
	 */
	public static final String ACTION_GLUE_RETURN = "GlueReturnWarehouse";

	/**
	 * Mes排料绑工单
	 */
	public static final String ACTION_BIND_WORKORDER = "BindWorkorder";

	/**
	 * Mes晶圆绑工单
	 */
	public static final String ACTION_BIND_WAFER_WORKORDER = "BindWaferWorkOrder";

	/**
	 * Mes晶圆解绑工单
	 */
	public static final String ACTION_UN_BIND_WAFER_WORKORDER = "UnBindWaferWorkOrder";

	/**
	 * Mes批次解绑工单
	 */
	public static final String ACTION_UN_BIND_MLOT_WORKORDER = "UnBindMLotWorkOrder";

	/**
	 * 晶圆Recon
	 */
	public static final String ACTION_UN_RECON_MLOT_UNIT = "ReconMLotUnit";

	/**
	 * 线边仓晶圆ENGHold
	 */
	public static final String ACTION_LSW_MLOT_UNIT_ENDHOLD_ = "LswMLotUnitEngHold";

	private MesSaveMLotHisRequestBody body;

}
