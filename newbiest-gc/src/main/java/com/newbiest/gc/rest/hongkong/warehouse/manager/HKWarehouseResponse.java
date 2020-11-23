package com.newbiest.gc.rest.hongkong.warehouse.manager;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class HKWarehouseResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private HKWarehouseResponseBody body;
	
}
