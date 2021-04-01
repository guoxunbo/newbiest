package com.newbiest.vanchip.rest.pack.append;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class AppendPackMaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "AppendPackMaterialLot";

	private AppendPackMaterialLotRequestBody body;

}
