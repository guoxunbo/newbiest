package com.newbiest.gc.rest.wltBox.print.parameter.wltLot.print.parameter;

import com.google.common.collect.Lists;
import com.newbiest.msg.ResponseBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class GcGetWltBoxPrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("client打印所需参数")
	private List<Map<String, Object>> parameterMapList;

	@ApiModelProperty("client打印所需标识，true才能触发打印")
	private Boolean clientPrint;

	public void settingClientPrint(List<Map<String, Object>> parameterMapList){
		this.parameterMapList = parameterMapList;
		if (parameterMapList.size() > 0) {
			clientPrint = true;
		}
	}
}
