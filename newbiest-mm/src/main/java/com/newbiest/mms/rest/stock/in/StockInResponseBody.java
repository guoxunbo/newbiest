package com.newbiest.mms.rest.stock.in;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.ResponseBody;
import lombok.Data;

import java.util.List;


@Data
public class StockInResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	List<MaterialLot> materialLots;

}
