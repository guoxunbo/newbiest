package com.newbiest.kms.rest.questionline;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class QuestionLineRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "QuestionLineManage";
	public static final String ACTION_GET_BY_QUESTION_RRN = "GetByQuestionRrn";

	private QuestionLineRequestBody body;

}
