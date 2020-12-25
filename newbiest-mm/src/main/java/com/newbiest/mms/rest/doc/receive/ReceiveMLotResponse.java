package com.newbiest.mms.rest.doc.receive;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ReceiveMLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ReceiveMLotResponseBody body;
	
}
