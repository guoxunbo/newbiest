package com.newbiest.vanchip.rest.labmaterial;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class LabMaterialRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "RawMaterialManage";

	@ApiModelProperty("导入保存")
	public static final String ACTION_IMPORT = "import";

	@ApiModelProperty("添加，修改")
	public static final String ACTION_MERGE = "merge";

	private LabMaterialRequestBody body;

}
