package com.newbiest.gc.rest.hongkong.warehouse.manager;

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
public class HKWarehouseRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "单据详情")
	private List<DocumentLine> documentLines;

	@ApiModelProperty(value = "真空包信息")
	private MaterialLot queryMaterialLot;

	@ApiModelProperty(value = "待出货的批次")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value="动态表主键")
	private Long tableRrn;

	@ApiModelProperty(value = "真空包号")
	private String queryLotId;

	@ApiModelProperty(value = "单据信息")
	private DocumentLine documentLine;

}
