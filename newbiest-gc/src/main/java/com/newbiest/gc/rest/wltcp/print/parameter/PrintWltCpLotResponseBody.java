package com.newbiest.gc.rest.wltcp.print.parameter;

import com.google.common.collect.Maps;
import com.newbiest.msg.ResponseBody;
import lombok.Data;
import java.util.Map;


@Data
public class PrintWltCpLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> parameterMap = Maps.newHashMap();

	private Boolean clientPrint;

	public void settingClientPrint(Map<String, Object> parameterMap){
		this.parameterMap = parameterMap;
		if (parameterMap.size() > 0){
			this.clientPrint = true;
		}
	}

}
