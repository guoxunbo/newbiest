package com.newbiest.mms.rest.partsMaterial;

import com.newbiest.mms.model.Parts;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoZhang Luo on 2019/9/3.
 */
@Data
@ApiModel("具体请求操作信息")
public class PartsMaterialRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Create/Update等")
	private String actionType;

	@ApiModelProperty(value = "操作的备件对象")
	private Parts parts;

}
