package com.newbiest.gc.rest.stockout.check;

import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.gc.model.StockOutCheck;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;


@Data
public class StockOutCheckResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<NBOwnerReferenceList> stockOutCheckList;

	private List<NBOwnerReferenceList> wltStockOutCheckList;

}
