package com.newbiest.mms.rest.materiallot.iqc;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class MaterialLotIqcRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private MaterialLotAction materialLotAction;

}
