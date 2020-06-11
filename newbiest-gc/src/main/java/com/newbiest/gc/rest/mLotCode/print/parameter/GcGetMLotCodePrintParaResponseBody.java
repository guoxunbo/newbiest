package com.newbiest.gc.rest.mLotCode.print.parameter;

import com.google.common.collect.Lists;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class GcGetMLotCodePrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, String>> parameterMapList = Lists.newArrayList();

}
