package com.newbiest.gc.rest.waferUnpack;

import com.google.common.collect.Lists;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Youqing Huang 20210915
 */
@Data
public class WaferUnpackResponseBody extends ResponseBody {

	private MaterialLotUnit materialLotUnit;

	private Map<String, Object> parameterMap;

	private List<Map<String, Object>> parameterMapList = Lists.newArrayList();

	private Boolean clientPrint;

	public void settingClientPrint(Map<String, Object> parameterMap){
		this.parameterMap = parameterMap;
		if (parameterMap.size() > 0){
			this.clientPrint = true;
		}
	}

	public void settingClientPrint(List<Map<String, Object>> parameterMapList){
		this.parameterMapList = parameterMapList;
		if (parameterMapList.size() > 0){
			this.clientPrint = true;
		}
	}

}
