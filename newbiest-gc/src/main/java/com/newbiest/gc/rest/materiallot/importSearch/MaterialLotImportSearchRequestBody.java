package com.newbiest.gc.rest.materiallot.importSearch;

import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class MaterialLotImportSearchRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Hold/Release")
	private String actionType;

	@ApiModelProperty(example = "动态表主键")
	private Long tableRrn;
}
