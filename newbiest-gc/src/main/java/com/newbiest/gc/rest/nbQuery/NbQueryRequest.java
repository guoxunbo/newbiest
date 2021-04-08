package com.newbiest.gc.rest.nbQuery;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class NbQueryRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "NBQuery";

	private NbQueryRequestBody body;

}
