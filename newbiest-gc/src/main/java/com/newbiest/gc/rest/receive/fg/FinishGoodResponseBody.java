package com.newbiest.gc.rest.receive.fg;

import com.google.common.collect.Lists;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class FinishGoodResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, String>> parameterMapList = Lists.newArrayList();

}
