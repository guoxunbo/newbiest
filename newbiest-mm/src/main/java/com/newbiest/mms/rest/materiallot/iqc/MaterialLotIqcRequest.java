package com.newbiest.mms.rest.materiallot.iqc;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotIqcRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotIQC";

	private MaterialLotIqcRequestBody body;

}
