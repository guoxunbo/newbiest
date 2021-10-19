package com.newbiest.vanchip.rest.doc.delivery.delete;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class DeleteDeliveryOrderResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private DeleteDeliveryOrderResponseBody body;
	
}
