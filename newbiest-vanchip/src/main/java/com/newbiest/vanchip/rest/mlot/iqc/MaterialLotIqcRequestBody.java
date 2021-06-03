package com.newbiest.vanchip.rest.mlot.iqc;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class MaterialLotIqcRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	private MaterialLotAction materialLotAction;

	private List<String> materialLotIds;

	private List<MaterialLotAction> materialLotActions;

}
