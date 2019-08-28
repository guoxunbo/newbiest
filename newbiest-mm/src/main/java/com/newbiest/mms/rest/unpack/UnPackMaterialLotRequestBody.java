package com.newbiest.mms.rest.unpack;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UnPackMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private List<MaterialLotAction> materialLotActions;

}
