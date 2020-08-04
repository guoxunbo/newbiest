package com.newbiest.gc.rest.record.express;

import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class RecordExpressResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLot> materialLots;

	/**
	 * 老记录快递使用
	 */
	@Deprecated
	private List<DeliveryOrder> deliveryOrderList;

}
