package com.newbiest.vanchip.rest.doc.returnlot.mlot;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ReturnByDocResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ReturnByDocResponseBody body;
	
}
