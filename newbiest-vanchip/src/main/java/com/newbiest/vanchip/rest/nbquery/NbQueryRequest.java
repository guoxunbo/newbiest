package com.newbiest.vanchip.rest.nbquery;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class NbQueryRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "NBQuery";

	private NbQueryRequestBody body;

}
