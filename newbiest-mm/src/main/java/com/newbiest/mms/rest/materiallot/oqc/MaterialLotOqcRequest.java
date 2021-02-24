package com.newbiest.mms.rest.materiallot.oqc;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotOqcRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotOQC";

	private MaterialLotOqcRequestBody body;

}
