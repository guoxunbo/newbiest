package com.newbiest.gc.rest.wltcp.print.parameter;

import com.google.common.collect.Maps;
import com.newbiest.msg.ResponseBody;
import lombok.Data;
import java.util.Map;


@Data
public class PrintWltCpLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Map<String, String> parameterMap = Maps.newHashMap();
}
