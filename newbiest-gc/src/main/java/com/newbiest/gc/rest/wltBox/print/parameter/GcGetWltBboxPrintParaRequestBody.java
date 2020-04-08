package com.newbiest.gc.rest.wltBox.print.parameter;

import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel("具体请求操作信息")
public class GcGetWltBboxPrintParaRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private Long materialLotRrn;

}
