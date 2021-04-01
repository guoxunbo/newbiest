package com.newbiest.vanchip.rest.pack.packCheck;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PackCheckRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("操作类型")
	private String actionType;

	@ApiModelProperty("Reel Code批次")
	private List<MaterialLot> materialLots;

	@ApiModelProperty("外箱批次号")
	private MaterialLotAction materialLotAction;
}
