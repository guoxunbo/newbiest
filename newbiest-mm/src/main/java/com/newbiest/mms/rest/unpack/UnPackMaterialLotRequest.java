package com.newbiest.mms.rest.unpack;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class UnPackMaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "UnPackMaterialLot";

	private UnPackMaterialLotRequestBody body;

}
