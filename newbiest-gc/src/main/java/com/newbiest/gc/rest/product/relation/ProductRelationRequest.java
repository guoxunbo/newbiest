package com.newbiest.gc.rest.product.relation;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by GuozhangLuo on 2020/11/03.
 */
@Data
@ApiModel
public class ProductRelationRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ProductRelationManage";

	public static final String ACTION_TYPE_SAVE_PRODUCT_NUMBER_RELATION = "SaveProductNumberRelation";

	public static final String ACTION_TYPE_UPDATE_PRODUCT_NUMBER_RELATION = "UpdateProductNumberRelation";

	private ProductRelationRequestBody body;



}
