package com.newbiest.gc.rest.product.relation;

import com.newbiest.gc.model.GCProductNumberRelation;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

/**
 * Created by guozhangLuo
 */
@Data
public class ProductRelationResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private GCProductNumberRelation productNumberRelation;

}
