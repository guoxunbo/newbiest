package com.newbiest.gc.rest.scm.assign;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class AssignRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ScmAssign";

	public static final String ACTION_TYPE_ASSIGN = "Assign";
	public static final String ACTION_TYPE_UN_ASSIGN = "UnAssign";

	private AssignRequestBody body;

}
