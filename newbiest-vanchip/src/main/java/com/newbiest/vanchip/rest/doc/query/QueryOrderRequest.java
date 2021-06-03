package com.newbiest.vanchip.rest.doc.query;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel
public class QueryOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "QueryOrderManager";

	@ApiParam("查询单据绑定的物料批次")
	public static final String ACTION_QUERY_MLOT_BY_ORDER = "queryMLotByOrder";

	@ApiParam("查询单据以及物料批次by物料批次号")
	public static final String ACTION_QUERY_ORDER_BY_MLOT_ID = "queryOrderByMLotId";

	@ApiParam("删除单据信息操作")
	public static final String ACTION_DELETE_DOCUMENT = "deleteDocument";

	private QueryOrderRequestBody body;

}
