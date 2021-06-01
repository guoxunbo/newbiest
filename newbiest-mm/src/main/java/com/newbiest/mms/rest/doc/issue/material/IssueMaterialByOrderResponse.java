package com.newbiest.mms.rest.doc.issue.material;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IssueMaterialByOrderResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private IssueMaterialByOrderResponseBody body;
	
}
