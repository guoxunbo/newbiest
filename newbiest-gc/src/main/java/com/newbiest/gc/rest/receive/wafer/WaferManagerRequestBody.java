package com.newbiest.gc.rest.receive.wafer;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class WaferManagerRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	@ApiModelProperty(value = "待处理的物料批次")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "单据")
	private List<DocumentLine> documentLines;

	@ApiModelProperty(value="动态表主键")
	private Long tableRrn;

	@ApiModelProperty(value = "查询条件")
	private String whereClause;

	@ApiModelProperty(value = "发料绑定单据")
	private String issueWithDoc;

	@ApiModelProperty(value = "发料计划投批")
	private String unPlanLot;

	@ApiModelProperty(value = "接收匹配单据")
	private String receiveWithDoc;

	@ApiModelProperty(value = "物料批次号")
	private String lotId;
}
