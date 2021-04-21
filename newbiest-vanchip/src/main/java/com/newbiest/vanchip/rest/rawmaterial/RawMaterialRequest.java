package com.newbiest.vanchip.rest.rawmaterial;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class RawMaterialRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "RawMaterialManage";

	@ApiModelProperty("导入保存")
	public static final String ACTION_IMPORT_SAVE = "importSave";

	@ApiModelProperty("添加，修改")
	public static final String ACTION_MERGE = "merge";

	private RawMaterialRequestBody body;

}
