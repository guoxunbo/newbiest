package com.newbiest.mms.rest.doc.issue.material;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IssueMaterialByOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IssueMaterialByOrder";

	public static final String ACTION_TYPE_RECOMMEND_ISSUE_ORDER = "recommendIssueOrder";
	public static final String ACTION_TYPE_ISSUE_MATERIAL_BY_ORDER = "issueMaterialByOrder";
	public static final String ACTION_TYPE_GET_MATERIAL_STOCK_QTY = "getMaterialStockQty";

	private IssueMaterialByOrderRequestBody body;

}
