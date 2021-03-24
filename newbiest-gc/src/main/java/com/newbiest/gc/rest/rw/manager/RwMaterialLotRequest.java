package com.newbiest.gc.rest.rw.manager;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class RwMaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCRwMLotManager";

	public static final String ACTION_QUERY_PRINT_PARAMETER = "getPrintParameter";

	private RwMaterialLotRequestBody body;

}
