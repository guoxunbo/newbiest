package com.newbiest.gc.rest.record.express;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class RecordExpressRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "记录的单据")
	private List<DeliveryOrder> deliveryOrderList;

}
