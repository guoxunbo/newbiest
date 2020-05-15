package com.newbiest.gc.rest.boxQRCode.print.paamater;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("具体请求操作信息")
public class GcGetBoxQRCodePrintParaRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "PrintLabel/PrintQRCodeLabel")
	private String actionType;

	@ApiModelProperty(value = "物料批次信息")
	private MaterialLot materialLot;

	@ApiModelProperty(value = "是否打印箱中真空包标签")
	private String printVboxLabelFlag;
}
