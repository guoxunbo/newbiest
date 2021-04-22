package com.newbiest.vanchip.rest.rawmaterial;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.RawMaterial;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class RawMaterialRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "操作的物料对象")
	private List<RawMaterial> dataList;

}
