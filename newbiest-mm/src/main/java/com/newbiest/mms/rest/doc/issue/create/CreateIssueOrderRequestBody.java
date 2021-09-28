package com.newbiest.mms.rest.doc.issue.create;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class CreateIssueOrderRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "单据号")
	private String documentId;

	@ApiModelProperty(value = "物料批次号")
	private List<String> materialLotIdList;

	@ApiModelProperty(value = "操作的物料以及pickQty")
	private List<Material> materials;

	@ApiModelProperty(value = "操作的物料批次以及pickQty")
	private List<MaterialLot> materialLots;

	@ApiModelProperty(value="动作原因/备注")
	private MaterialLotAction materialLotAction;

	private String materialName;
	//领料人
	private String creator;

	private BigDecimal qty;
	//备注
	private String partComments;
}
