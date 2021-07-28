package com.newbiest.gc.rest.product.relation;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guozhangLuo
 */
@Data
public class ProductRelationResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ProductRelationResponseBody body;
	
}
