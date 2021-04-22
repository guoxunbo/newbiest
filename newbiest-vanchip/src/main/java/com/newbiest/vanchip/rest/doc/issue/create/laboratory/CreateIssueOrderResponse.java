package com.newbiest.vanchip.rest.doc.issue.create.laboratory;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class CreateIssueOrderResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private CreateIssueOrderResponseBody body;
	
}
