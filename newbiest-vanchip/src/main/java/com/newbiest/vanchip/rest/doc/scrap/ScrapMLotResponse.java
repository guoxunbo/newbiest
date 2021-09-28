package com.newbiest.vanchip.rest.doc.scrap;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ScrapMLotResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ScrapMLotResponseBody body;
	
}
