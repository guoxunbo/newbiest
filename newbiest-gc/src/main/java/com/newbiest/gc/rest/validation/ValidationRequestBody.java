package com.newbiest.gc.rest.validation;

import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.RequestBody;
import lombok.Data;


@Data
public class ValidationRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private DocumentLine documentLine;

	private MaterialLot materialLot;

}
