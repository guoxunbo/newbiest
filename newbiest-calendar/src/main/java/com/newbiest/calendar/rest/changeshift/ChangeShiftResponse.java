package com.newbiest.calendar.rest.changeshift;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class ChangeShiftResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ChangeShiftResponseBody body;
	
}
