package com.newbiest.mms.rest.doc.issue.create;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class CreateIssueOrderResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private CreateIssueOrderResponseBody body;
	
}
