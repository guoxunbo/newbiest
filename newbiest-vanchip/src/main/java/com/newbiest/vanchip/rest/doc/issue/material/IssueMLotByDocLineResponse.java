package com.newbiest.vanchip.rest.doc.issue.material;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IssueMLotByDocLineResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private IssueMLotByDocLineResponseBody body;
	
}
