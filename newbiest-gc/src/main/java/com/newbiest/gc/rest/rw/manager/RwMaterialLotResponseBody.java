package com.newbiest.gc.rest.rw.manager;

import com.google.common.collect.Lists;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RwMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, String>> parameterList = Lists.newArrayList();
}
