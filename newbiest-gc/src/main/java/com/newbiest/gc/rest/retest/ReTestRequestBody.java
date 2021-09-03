package com.newbiest.gc.rest.retest;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class ReTestRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "待重测发料的物料批次")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "单据")
	private List<DocumentLine> documentLines;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "手持端发料单据日期")
	private String erpTime;

}
