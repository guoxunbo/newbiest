package com.newbiest.vanchip.rest.doc.delivery.delete;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.DocumentLine;
import lombok.Data;

@Data
public class DeleteDeliveryOrderResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private DocumentLine documentLine;
}
