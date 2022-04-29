package com.newbiest.gc.rest.IRAPackage;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IRAPackageRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IRAPackageManager";

	private IRAPackageRequestBody body;

}
