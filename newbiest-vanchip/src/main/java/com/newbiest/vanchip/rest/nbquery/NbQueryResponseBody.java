package com.newbiest.vanchip.rest.nbquery;

import com.google.common.collect.Lists;
import com.newbiest.base.msg.ResponseBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NbQueryResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "数据总条数")
	Integer totalCount ;

	@ApiModelProperty(value = "返回的结果集")
	List<Map> mapList = Lists.newArrayList();
}
