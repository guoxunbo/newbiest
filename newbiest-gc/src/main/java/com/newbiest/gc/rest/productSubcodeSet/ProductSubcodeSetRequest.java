package com.newbiest.gc.rest.productSubcodeSet;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel
public class ProductSubcodeSetRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ProductSubcodeManage";

	private ProductSubcodeSetRequestBody body;

}
