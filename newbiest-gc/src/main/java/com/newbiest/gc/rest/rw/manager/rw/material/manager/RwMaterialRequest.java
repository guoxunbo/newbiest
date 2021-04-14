package com.newbiest.gc.rest.rw.manager.rw.material.manager;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class RwMaterialRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCRwMaterialManager";

	public static final String ACTION_TAPE_SCAN = "TapeScan";

	public static final String ACTION_TAPE_RECEIVE = "TapeReceive";

	public static final String ACTION_BLADE_RECEIVE = "BladeReceive";

	public static final String ACTION_GET_BLADE_MLOTID = "GetBladeMLotId";

	public static final String ACTION_BLADE_SCAN = "BladeScan";

	public static final String ACTION_MATERIAL_SPARE = "MaterialSpare";

	public static final String ACTION_MATERIAL_ISSUE = "MaterialIssue";

	private RwMaterialRequestBody body;

}
