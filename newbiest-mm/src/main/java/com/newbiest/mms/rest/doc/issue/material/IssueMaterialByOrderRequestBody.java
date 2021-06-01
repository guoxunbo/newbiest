package com.newbiest.mms.rest.doc.issue.material;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.Material;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class IssueMaterialByOrderRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "单据号")
	private String documentId;

	@ApiModelProperty(value = "物料批次号")
	private List<String> materialLotIds;

	private List<Material> materials;

}
