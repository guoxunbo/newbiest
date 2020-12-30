package com.newbiest.mms.rest.doc.issue.material;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IssueMaterialRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IssueMLotByMaterial";

	private IssueMaterialRequestBody body;

}
