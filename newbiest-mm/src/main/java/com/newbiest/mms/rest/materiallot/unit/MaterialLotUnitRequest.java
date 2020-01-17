package com.newbiest.mms.rest.materiallot.unit;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotUnitRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotUnitManage";


	private MaterialLotUnitRequestBody body;

}
