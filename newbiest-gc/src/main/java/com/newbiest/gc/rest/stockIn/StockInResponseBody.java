package com.newbiest.gc.rest.stockIn;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class StockInResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private MaterialLot materialLot;

	private List<MaterialLot> materialLotList;

}
