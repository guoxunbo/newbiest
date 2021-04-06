package com.newbiest.mms.rest.materiallot.split.standard;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class SplitStandardMaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "SplitStandardMaterialLot";

	private SplitStandardMaterialLotRequestBody body;

}
