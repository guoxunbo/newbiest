package com.newbiest.mms.rest.doc.issue.mlot;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IssueMLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private IssueMLotResponseBody body;
	
}
