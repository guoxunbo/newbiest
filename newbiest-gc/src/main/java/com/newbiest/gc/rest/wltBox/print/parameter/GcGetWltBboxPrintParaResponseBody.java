package com.newbiest.gc.rest.wltBox.print.parameter;

import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class GcGetWltBboxPrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Map<String, String> parameters = new HashMap<>();
}
