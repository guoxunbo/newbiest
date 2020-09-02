package com.newbiest.mms.rest.rawmaterial;

import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.Parts;
import com.newbiest.mms.model.RawMaterial;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel("具体请求操作信息")
public class RawMaterialRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Create/Update等")
	private String actionType;

	@ApiModelProperty(value = "操作的物料对象")
	private RawMaterial material;

	@ApiModelProperty(value = "操作的备件对象")
	private Parts parts;

}
