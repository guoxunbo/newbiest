package com.newbiest.gc.rest.rw.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RwMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, Object>> parameterMapList = Lists.newArrayList();

	private Map<String, Object> parameterMap = Maps.newHashMap();

	private Boolean clientPrint;

	private List<MaterialLot> materialLotList = Lists.newArrayList();
	private List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();

	private MaterialLot materialLot;

    private boolean falg;

	public void settingClientPrint(List<Map<String, Object>> parameterMapList){
		this.parameterMapList = parameterMapList;
		if (parameterMapList.size() > 0) {
			this.clientPrint = true;
		}
	}

	public void settingClientPrint(Map<String, Object> parameterMap){
		this.parameterMap  = parameterMap;
		if (parameterMap.size() > 0) {
			this.clientPrint = true;
		}
	}
}
