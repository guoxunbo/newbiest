package com.newbiest.gc.rest.rw.manager;

import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class RwMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Query/StockIn")
	private String actionType;

	@ApiModelProperty(value = "物料批次号")
	private List<MaterialLot> materialLotList;

	@ApiModelProperty(value = "完成品")
	private List<MesPackedLot> mesPackedLots;

	@ApiModelProperty(value = "打印标签")
	private String printLabel;

	@ApiModelProperty(value = "物料批次号")
	private MaterialLot materialLot;

    @ApiModelProperty(value = "挑选所需数量")
    private BigDecimal pickQty;

	@ApiModelProperty(value = "客户标识")
	private String customerName;

	@ApiModelProperty(value = "简称")
	private String abbreviation;

	@ApiModelProperty(value = "备注")
	private String remarks;

	@ApiModelProperty(value = "出货单号")
	private String shipOrderId;

	@ApiModelProperty(value = "表单主键")
	private Long tableRrn;

	@ApiModelProperty(value = "CstId/BoxId")
	private String queryLotId;

	@ApiModelProperty(value = "出货单据")
	private List<DocumentLine> documentLineList;

	@ApiModelProperty(value = "物料主键")
	private Long materialLotRrn;

	@ApiModelProperty(value = "标签打印份数")
	private String printCount;

	@ApiModelProperty(value = "查询条件")
	private String whereClause;

	@ApiModelProperty(value = "晶圆列表")
	private List<MaterialLotUnit> materialLotUnitList;

}
