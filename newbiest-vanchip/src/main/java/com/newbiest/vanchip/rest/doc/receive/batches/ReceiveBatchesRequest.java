package com.newbiest.vanchip.rest.doc.receive.batches;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ReceiveBatchesRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ReceiveMaterialLotByDoc";

	public static final String ACTION_TYPE_BATCHES = "Batches";

	private ReceiveBatchesRequestBody body;

}
