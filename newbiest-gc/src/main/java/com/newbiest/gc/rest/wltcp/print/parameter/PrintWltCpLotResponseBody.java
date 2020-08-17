package com.newbiest.gc.rest.wltcp.print.parameter;

import com.google.common.collect.Lists;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class PrintWltCpLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLotUnit> mLotUnitList = Lists.newArrayList();
}
