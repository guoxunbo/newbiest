package com.newbiest.vanchip.rest.nbquery;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;


@Data
@ApiModel("具体请求操作信息")
public class NbQueryRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "查询名称")
	private String queryName;

	private String queryText;

	private Map<String, Object> paramMap;

	private int firstResult;

	private int maxResult;

	private String whereClause;

	private String orderByClause;

}
