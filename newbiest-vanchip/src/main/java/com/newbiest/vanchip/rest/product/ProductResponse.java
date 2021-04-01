package com.newbiest.vanchip.rest.product;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ProductResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ProductResponseBody body;
	
}
