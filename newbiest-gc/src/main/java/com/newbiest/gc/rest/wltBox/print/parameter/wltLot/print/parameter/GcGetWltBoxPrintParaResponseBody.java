package com.newbiest.gc.rest.wltBox.print.parameter.wltLot.print.parameter;

import com.google.common.collect.Lists;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class GcGetWltBoxPrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, String>> parameterMapList = Lists.newArrayList();
}
