package com.newbiest.vanchip.rest.unpack;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import lombok.Data;

import java.util.List;

@Data
public class UnPackMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private List<MaterialLotAction> materialLotActions;

}
