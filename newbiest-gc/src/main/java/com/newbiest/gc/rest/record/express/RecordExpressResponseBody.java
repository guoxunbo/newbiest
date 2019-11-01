package com.newbiest.gc.rest.record.express;

import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.msg.ResponseBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RecordExpressResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<DeliveryOrder> deliveryOrderList;
}
