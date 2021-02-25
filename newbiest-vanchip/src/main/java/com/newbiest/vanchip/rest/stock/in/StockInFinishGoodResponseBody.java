package com.newbiest.vanchip.rest.stock.in;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;


@Data
public class StockInFinishGoodResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	List<MaterialLot> materialLots;

}
