package com.newbiest.vanchip.rest.labmaterial;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.LabMaterial;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("具体请求操作信息")
public class LabMaterialRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "待保存的数据")
	private LabMaterial material;

}
