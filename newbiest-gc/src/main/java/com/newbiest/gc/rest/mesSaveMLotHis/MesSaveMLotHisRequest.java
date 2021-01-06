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
	 * 保存物料批次历史
	 */
	public static final String ACTION_SAVE_MLOT_HIS = "SaveMLotHis";

	/**
	 * 保存晶圆历史
	 */
	public static final String ACTION_SAVE_MLOTUNIT_HIS = "SaveMLotUnitHis";

	private MesSaveMLotHisRequestBody body;

}