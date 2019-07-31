package com.newbiest.kms.rest.question;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class QuestionRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "QuestionManage";
	public static final String ACTION_CLOSE = "Close";

	private QuestionRequestBody body;

}
