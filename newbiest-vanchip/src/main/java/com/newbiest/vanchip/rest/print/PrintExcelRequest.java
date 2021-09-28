package com.newbiest.vanchip.rest.print;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PrintExcelRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "printExcelManager";

	@ApiModelProperty(value = "coc标签打印")
	public static final String ACTION_PRINT_COC = "PrintCoc";

	@ApiModelProperty(value = "装箱清单打印")
	public static final String ACTION_PRINT_PACKING_LIST = "PrintPackingList";

	@ApiModelProperty(value = "出货清单打印")
	public static final String ACTION_PRINT_SHIPPING_LIST = "PrintShippingList";

	@ApiModelProperty(value = "拣配表打印")
	public static final String ACTION_PRINT_PK_LIST = "PrintPKList";

	@ApiModelProperty(value = "装箱清单打印 和 coc标签打印")
	public static final String ACTION_PRINT_PACKING_LIST_AND_COC = "PrintPackingListAndCoc";

	@ApiModelProperty(value = "发货单通知单打印")
	public static final String ACTION_PRINT_DELIVERY_ORDER = "PrintDeliveryOrder";

	@ApiModelProperty(value = "打印退供应商单据和报废单据")
	public static final String ACTION_PRINT_RS_AND_SCRAP_ORDER = "PrintRSAndScrapOrder";

	private PrintExcelRequestBody body;

}
