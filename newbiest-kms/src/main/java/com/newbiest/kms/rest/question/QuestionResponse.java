package com.newbiest.kms.rest.question;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class QuestionResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private QuestionResponseBody body;
	
}
