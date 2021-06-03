package com.newbiest.vanchip.rest.print;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class PrintExcelResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private PrintExcelResponseBody body;
	
}
