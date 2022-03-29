package com.newbiest.gc.rest.reserved;

import com.newbiest.base.ui.model.NBTable;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class ReServedRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	public static final String ACTION_TYPE_GET_MATERIAL_LOT = "GetMLot";
	public static final String ACTION_TYPE_GET_OTHER_SHIP_MATERIAL_LOT = "GetOtherShipReservedMLot";
	public static final String ACTION_TYPE_GET_MATERIAL_LOT_AND_USER = "GetMLotAndUser";
	public static final String ACTION_TYPE_RESERVED = "Reserved";
	public static final String ACTION_TYPE_OTHER_SHIP_RESERVED = "OtherShipReserved";
	public static final String ACTION_TYPE_UN_RESERVED = "UnReserved";
	public static final String ACTION_GET_AUTO_PACK_MLOT = "GetAutoPackMLot";
	public static final String ACTION_GET_PACKED_RULE_LIST = "GetPackedRuleList";

	/**
	 * 获取包装箱里的批次
	 */
	public static final String ACTION_GET_PACKED_MLOTS = "GetPackedMLots";

	@ApiModelProperty(value="动作. GetMLot, Reserved")
	private String actionType;

	@ApiModelProperty(value="单据Line的主键")
	private Long docLineRrn;

	@ApiModelProperty(value="动态表主键")
	private Long tableRrn;

	@ApiModelProperty(value = "待备货的物料批次信息")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "备货备注")
	private String stockNote;

	@ApiModelProperty(example = "动态表")
	private NBTable table;

	@ApiModelProperty(example = "查询条件")
	private String whereClause;

	@ApiModelProperty(example = "包装规格")
	private String packageRule;

	@ApiModelProperty(example = "货架归属地")
	private String stockLocation;
}
