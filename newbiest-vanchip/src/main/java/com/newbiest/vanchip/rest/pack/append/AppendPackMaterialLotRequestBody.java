package com.newbiest.vanchip.rest.pack.append;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AppendPackMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "物料批次操作")
	private List<MaterialLotAction> waitToAppendActions;

	@ApiModelProperty(value = "包装批次")
	private MaterialLot packedMaterialLot;

}
