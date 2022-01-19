package com.newbiest.gc.rest.box.print.parameter;

import com.google.common.collect.Lists;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class GcGetBboxPrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Map<String, String> parameters = new HashMap<>();

	private Map<String, String> customerParameter = new HashMap<>();

	private List<Map<String, Object>> parameterMapList = Lists.newArrayList();

	private Boolean clientPrint;

	public void settingClientPrint(List<Map<String, Object>> parameterMapList){
		this.parameterMapList = parameterMapList;
		if (parameterMapList.size() > 0) {
			clientPrint = true;
		}
	}
}
