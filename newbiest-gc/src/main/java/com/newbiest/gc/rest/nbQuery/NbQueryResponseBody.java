package com.newbiest.gc.rest.nbQuery;

import com.google.common.collect.Lists;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NbQueryResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	List<Map> mapList = Lists.newArrayList();
}
