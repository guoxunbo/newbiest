package com.newbiest.vanchip.rest.print;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class PrintExcelResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Map<String, Object> parameterMap;

	private List<Map<String, Object>> parameterList;

	private List<MaterialLot> materialLots;
}
