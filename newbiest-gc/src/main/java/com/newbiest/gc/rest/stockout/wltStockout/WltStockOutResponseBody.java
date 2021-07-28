package com.newbiest.gc.rest.stockout.wltStockout;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class WltStockOutResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private boolean falg;

	private List<MaterialLotUnit> materialLotUnitList;

	private MaterialLot materialLot;

}
