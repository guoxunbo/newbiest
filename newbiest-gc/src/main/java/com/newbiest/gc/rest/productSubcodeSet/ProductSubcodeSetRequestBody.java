package com.newbiest.gc.rest.productSubcodeSet;

import com.newbiest.gc.model.GCProductSubcode;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel("具体请求操作信息")
public class ProductSubcodeSetRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Create/Update等")
	private String actionType;

	@ApiModelProperty(value = "操作产品二级代码信息")
	private GCProductSubcode productSubcode;

}
