package com.newbiest.gc.rest.receive.rma;

import com.google.common.collect.Lists;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by guozhangLuo on 2020/12/14.
 */
@Data
public class RMAMLotManagerResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<Map<String, String>> parameterMapList = Lists.newArrayList();

}
