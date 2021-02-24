package com.newbiest.vanchip.rest.stock.in;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StockInRequest extends Request {

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE_NAME = "MaterialLotStockIn";

	private StockInRequestBody body;

}
