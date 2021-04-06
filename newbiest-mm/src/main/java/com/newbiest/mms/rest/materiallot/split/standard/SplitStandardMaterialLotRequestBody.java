package com.newbiest.mms.rest.materiallot.split.standard;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("具体请求操作信息")
public class SplitStandardMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private MaterialLotAction materialLotAction;

}
