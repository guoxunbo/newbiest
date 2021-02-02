package com.newbiest.vanchip.rest.doc.returnlot.mlot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ReturnMLotByDocRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ReturnMLotByDoc";

	private ReturnMLotByDocRequestBody body;

}
