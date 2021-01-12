package com.newbiest.mms.rest.iqc;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IqcCheckRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "IqcCheckManager";

	public static final String ACTION_IQC_CHECK = "IqcCheck";
	public static final String ACTION_GET_MLOT_CHECK_SHEET_LINE = "GetMLotCheckSheetLine";

	private IqcCheckRequestBody body;

}
