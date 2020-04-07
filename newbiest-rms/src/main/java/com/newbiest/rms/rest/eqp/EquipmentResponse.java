package com.newbiest.rms.rest.eqp;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class EquipmentResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private EquipmentResponseBody body;
	
}
