package com.newbiest.vanchip.rest.doc.create;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.DocumentLine;
import lombok.Data;

@Data
public class CreateOrderResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Document document;

	private DocumentLine documentLine;
}
