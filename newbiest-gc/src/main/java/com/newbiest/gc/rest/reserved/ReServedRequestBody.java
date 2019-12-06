package com.newbiest.gc.rest.reserved;

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
	public static final String ACTION_TYPE_RESERVED = "Reserved";
	public static final String ACTION_TYPE_UN_RESERVED = "UnReserved";

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

}
