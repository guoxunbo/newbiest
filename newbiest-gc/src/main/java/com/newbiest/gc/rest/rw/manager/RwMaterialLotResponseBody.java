package com.newbiest.gc.rest.rw.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RwMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, String>> parameterList = Lists.newArrayList();

	private Map<String, String> parameterMap = Maps.newHashMap();

	private List<MaterialLot> materialLotList = Lists.newArrayList();
}
