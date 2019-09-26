package com.newbiest.gc.rest.box.print.parameter;

import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class GcGetBboxPrintParaResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Map<String, String> parameters = new HashMap<>();
}
