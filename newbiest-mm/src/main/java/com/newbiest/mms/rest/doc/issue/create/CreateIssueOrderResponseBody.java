package com.newbiest.mms.rest.doc.issue.create;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Document;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateIssueOrderResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private String documentId;

	private Document document;

	@ApiModelProperty("成本中心")
	private String costCenter;

}
