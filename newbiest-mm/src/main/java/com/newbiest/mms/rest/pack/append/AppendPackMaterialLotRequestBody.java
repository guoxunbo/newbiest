package com.newbiest.mms.rest.pack.append;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.RequestBody;
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
