package com.newbiest.gc.rest.vboxHoldSet;

import com.newbiest.gc.model.GCWorkorderRelation;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guozhangluo 20201013
 */
@Data
@ApiModel("具体请求操作信息")
public class VboxHoldSetRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Create/Update等")
	private String actionType;

	@ApiModelProperty(value = "操作工单等级Hold信息")
	private GCWorkorderRelation workorderRelation;

}
