package com.newbiest.gc.scm.send.mlot.state;

import com.newbiest.msg.RequestBody;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class MaterialLotStateReportRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	public static final String ACTION_TYPE_HOLD = "Hold";
	public static final String ACTION_TYPE_RELEASE = "Release";
	public static final String ACTION_TYPE_PLAN = "Plan";
	public static final String ACTION_TYPE_UN_PLAN = "UnPlan";


	private String actionType;

	private List<Map<String, String>> materialLotList;
}
