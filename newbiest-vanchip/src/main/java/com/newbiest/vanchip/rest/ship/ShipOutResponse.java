package com.newbiest.vanchip.rest.ship;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class ShipOutResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private ShipOutResponseBody body;
	
}
