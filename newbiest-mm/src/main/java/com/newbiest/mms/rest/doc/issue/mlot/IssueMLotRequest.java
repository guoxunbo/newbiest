package com.newbiest.mms.rest.doc.issue.mlot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IssueMLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IssueMLotByMaterial";

	private IssueMLotRequestBody body;

}
