package com.newbiest.kms.rest.questionline;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class QuestionLineResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private QuestionLineResponseBody body;
	
}
