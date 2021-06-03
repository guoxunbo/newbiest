package com.newbiest.vanchip.rest.partsmaterial;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.Parts;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class PartsMaterialRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Create/Update等")
	private String actionType;

	@ApiModelProperty(value = "操作的备件")
	private List<Parts> dataList;

	@ApiModelProperty(value = "操作的备件批次")
	private MaterialLot materialLot;

	@ApiModelProperty(value = "操作的备件动作")
	private MaterialLotAction materialLotAction;

}
