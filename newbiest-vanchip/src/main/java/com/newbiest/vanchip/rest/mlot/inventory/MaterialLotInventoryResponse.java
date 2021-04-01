package com.newbiest.vanchip.rest.mlot.inventory;

import com.newbiest.base.msg.Response;
import lombok.Data;


@Data
public class MaterialLotInventoryResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private MaterialLotInventoryResponseBody body;
	
}
