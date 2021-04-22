package com.newbiest.vanchip.rest.doc.issue.create.laboratory;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Document;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateIssueOrderResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("单据信息")
	private Document document;
}
