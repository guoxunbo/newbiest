package com.newbiest.vanchip.rest.product;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Product;
import lombok.Data;

@Data
public class ProductResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Product material;

}
