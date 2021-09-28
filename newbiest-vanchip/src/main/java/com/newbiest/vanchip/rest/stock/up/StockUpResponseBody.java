package com.newbiest.vanchip.rest.stock.up;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;


@Data
public class StockUpResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	List<MaterialLot> materialLotList;

}
