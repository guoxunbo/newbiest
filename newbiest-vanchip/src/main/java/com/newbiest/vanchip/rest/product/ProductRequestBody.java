package com.newbiest.vanchip.rest.product;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class ProductRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "merge/import")
	private String actionType;

	@ApiModelProperty(value = "操作的物料对象")
	private List<Product> dataList;

}
