package com.newbiest.gc.rest.hongkong.warehouse.manager;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class HKWarehouseResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private boolean falg;

	private MaterialLot materialLot;

}
