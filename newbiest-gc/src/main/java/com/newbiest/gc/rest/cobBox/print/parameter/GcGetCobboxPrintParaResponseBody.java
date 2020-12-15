package com.newbiest.gc.rest.cobBox.print.parameter;

import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class GcGetCobboxPrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Map<String, String> parameters = new HashMap<>();
}
