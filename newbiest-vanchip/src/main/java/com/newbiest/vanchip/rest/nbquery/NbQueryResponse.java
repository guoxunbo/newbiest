package com.newbiest.vanchip.rest.nbquery;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class NbQueryResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private NbQueryResponseBody body;
	
}
