package com.newbiest.gc.rest.stockout.wltStockout;

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
public class WltStockOutRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "单据详情")
	private List<DocumentLine> documentLines;

	@ApiModelProperty(value = "箱信息")
	private MaterialLot queryMaterialLot;

	@ApiModelProperty(value = "待出货的批次")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "出货标注备注")
	private String stockTagNote;

	@ApiModelProperty(value = "客户简称")
	private String customerName;

	@ApiModelProperty(value = "出货形态")
	private String stockOutType;

	@ApiModelProperty(value = "PO号")
	private String poId;

	@ApiModelProperty(value="动态表主键")
	private Long tableRrn;

	@ApiModelProperty(value = "物料批次号/LOTID")
	private String queryLotId;
  
	@ApiModelProperty(value = "三方销售单据")
	private DocumentLine documentLine;
  
  @ApiModelProperty(value = "检验二级代码")
	private String checkSubCode;

	@ApiModelProperty(value = "供应商地址")
	private String address;

	@ApiModelProperty(value = "选择的物料批次")
	private List<MaterialLot> materialLots;

	@ApiModelProperty(value = "手持端发料单据日期")
	private String erpTime;
  
}
