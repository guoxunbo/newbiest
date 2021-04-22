package com.newbiest.vanchip.rest.doc.issue.labmlot;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IssueLabMLotOrderResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private IssueLabMLotOrderResponseBody body;
	
}
