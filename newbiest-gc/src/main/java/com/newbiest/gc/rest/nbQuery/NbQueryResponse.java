package com.newbiest.gc.rest.nbQuery;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class NbQueryResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private NbQueryResponseBody body;
	
}
