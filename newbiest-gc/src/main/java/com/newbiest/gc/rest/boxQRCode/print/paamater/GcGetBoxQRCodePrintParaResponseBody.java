package com.newbiest.gc.rest.boxQRCode.print.paamater;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class GcGetBoxQRCodePrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, Object>> parameterMapList = Lists.newArrayList();

	private Map<String, Object> parameterMap = Maps.newHashMap();

	private Boolean clientPrint;

	public void settingClientPrint(List<Map<String, Object>> parameterMapList){
		this.parameterMapList = parameterMapList;
		if (parameterMapList.size() > 0) {
			this.clientPrint = true;
		}
	}

	public void settingClientPrint(Map<String, Object> parameterMap){
		this.parameterMap = parameterMap;
		if (parameterMap.size() > 0) {
			this.clientPrint = true;
		}
	}

}
