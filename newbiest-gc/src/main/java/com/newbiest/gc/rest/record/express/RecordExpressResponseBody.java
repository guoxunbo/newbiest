package com.newbiest.gc.rest.record.express;

import com.google.common.collect.Lists;
import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RecordExpressResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLot> materialLots;

	private List<Map<String, String>> parameterMapList = Lists.newArrayList();

	/**
	 * 老记录快递使用
	 */
	@Deprecated
	private List<DeliveryOrder> deliveryOrderList;

}
