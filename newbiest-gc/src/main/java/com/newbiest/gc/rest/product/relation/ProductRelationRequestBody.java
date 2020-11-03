package com.newbiest.gc.rest.product.relation;

import com.newbiest.gc.model.GCProductNumberRelation;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guozhangLuo
 */
@Data
@ApiModel("具体请求操作信息")
public class ProductRelationRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Create/Update等")
	private String actionType;

	@ApiModelProperty(value = "产品箱数量配置")
	private GCProductNumberRelation productNumberRelation;

}
