package com.newbiest.mms.rest.materiallot.oqc;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class MaterialLotOqcRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private MaterialLotAction materialLotAction;

}
