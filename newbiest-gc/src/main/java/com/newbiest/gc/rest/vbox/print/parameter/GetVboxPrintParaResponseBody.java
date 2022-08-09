package com.newbiest.gc.rest.vbox.print.parameter;

import com.google.common.collect.Lists;
import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class GetVboxPrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, Object>> parameterMapList = Lists.newArrayList();

	private MesPackedLot mesPackedLot;

	private Boolean clientPrint;

	public void settingClientPrint(List<Map<String, Object>> parameterMapList){
		this.parameterMapList = parameterMapList;
		if (parameterMapList.size() > 0) {
			clientPrint = true;
		}
	}
}
