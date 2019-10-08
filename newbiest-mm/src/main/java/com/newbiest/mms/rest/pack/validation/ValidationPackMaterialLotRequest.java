package com.newbiest.mms.rest.pack.validation;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class ValidationPackMaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ValidationPackMaterialLot";

	/**
	 * 验证包装
	 */
	public static final String ACTION_VALIDATION_PACK = "ValidationPack";

	/**
	 * 验证追加包装
	 */
	public static final String ACTION_VALIDATION_APPEND = "ValidationAppend";

	private ValidationPackMaterialLotRequestBody body;

}
