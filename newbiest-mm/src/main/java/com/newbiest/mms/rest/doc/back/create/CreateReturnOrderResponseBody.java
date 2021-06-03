package com.newbiest.mms.rest.doc.back.create;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Document;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateReturnOrderResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "单据")
	private Document document;

}
