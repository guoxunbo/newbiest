package com.newbiest.kms.rest.questionline;

import com.newbiest.kms.model.Question;
import com.newbiest.kms.model.QuestionLine;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class QuestionLineResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<QuestionLine> questionLines;

}
