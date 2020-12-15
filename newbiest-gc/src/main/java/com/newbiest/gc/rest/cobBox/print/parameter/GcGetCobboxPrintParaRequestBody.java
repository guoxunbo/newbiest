package com.newbiest.gc.rest.cobBox.print.parameter;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("具体请求操作信息")
public class GcGetCobboxPrintParaRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private MaterialLot materialLot;

}
