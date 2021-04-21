package com.newbiest.vanchip.rest.product;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@Data
@ApiModel
public class ProductRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ProductManager";

	public static final String ACTION_IMPORT_SAVE = "importSave";
	public static final String ACTION_MERGE = "merge";

	private ProductRequestBody body;

}
