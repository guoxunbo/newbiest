package com.newbiest.vanchip.rest.doc.returnlot.create;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("具体请求操作信息")
public class CreateReturnMLotOrderRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value = "单据号")
	private String documentId;

	@ApiModelProperty(value = "物料批次号以及数量以及退料原因")
	private List<Map<String, String>> materialLotIdAndQtyAndReasonMapList ;

}
