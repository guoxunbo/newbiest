package com.newbiest.vanchip.rest.doc.receive.batches;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ReceiveBatchesResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ReceiveBatchesResponseBody body;
	
}
