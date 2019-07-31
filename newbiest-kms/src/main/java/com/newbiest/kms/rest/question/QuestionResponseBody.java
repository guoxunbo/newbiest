package com.newbiest.kms.rest.question;

import com.newbiest.kms.model.Question;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class QuestionResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Question question;

}
