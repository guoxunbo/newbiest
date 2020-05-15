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

	private List<Map<String, String>> parameterMapList = Lists.newArrayList();

	private Map<String, String> parameterMap = Maps.newHashMap();


}
