package com.newbiest.gc.rest.record.express;

import com.newbiest.gc.express.dto.OrderInfo;
import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class RecordExpressRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 自动下单，则需要关联跨域速递进行下单
	 */
	public static final String ACTION_TYPE_AUTO_ORDER = "AutoOrder";

	/**
	 * 手动下单，则只需要更新物料批次和单据的信息即可
	 */
	public static final String ACTION_TYPE_MANUAL_ORDER = "ManualOrder";

	public static final String ACTION_TYPE_CANCEL_ORDER = "CancelOrder";

	public static final String ACTION_TYPE_OLD_RECORD_ORDER = "OldRecordOrder";

	public static final String ACTION_TYPE_QUERY_PRINTPARAMETER = "QueryPrintParameter";

	public static final String ACTION_TYPE_OBLIQUE_LABEL_PRINT = "ObliqueLabelPrint";

	public static final String ACTION_TYPE_BATCH_CANCEL_ORDER = "BatchCancelOrder";

	public static final String ACTION_TYPE_QUERY_ORDER_INFO = "QueryOrderInfo";

	public static final String ACTION_TYPE_SAMSUNG_OUTER_BOX_LABEL_PRINT = "SamsungOuterBoxLabelPrint";

	private String actionType;

	private Integer serviceMode;

	private Integer payMode;

	public String expressNumber;

	public String expressCompany;

	private List<MaterialLot> materialLots;

	private String wayBillNumber;

	@ApiModelProperty(value = "下单时间")
	private String orderTime;

	/**
	 * 老版本的记录快递接口使用
	 */
	@Deprecated
	private List<DocumentLine> documentLineList;

	@ApiModelProperty(value = "快递单信息")
	private List<OrderInfo> orderList;

	@ApiModelProperty(value = "快递单信息")
	private String printCount;

}
