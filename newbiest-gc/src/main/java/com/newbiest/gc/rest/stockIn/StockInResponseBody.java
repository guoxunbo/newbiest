package com.newbiest.gc.rest.stockIn;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.ResponseBody;
import lombok.Data;

@Data
public class StockInResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MaterialLot materialLot;

}
