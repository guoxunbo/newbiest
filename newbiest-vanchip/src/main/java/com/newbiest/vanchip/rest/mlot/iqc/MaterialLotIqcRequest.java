package com.newbiest.vanchip.rest.mlot.iqc;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotIqcRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotIQC";

	public static final String ACTIONT_BATCH_IQC = "BatchIqc";
	public static final String ACTION_VALIDATION_AND_GET_MLOT = "ValidationAndGetWaitIqcMLot";
	public static final String ACTION_GET_CHECK_SHEET_LINE = "GetCheckSheetLine";
	public static final String ACTIONT_IQC_APPROVAL = "IqcApproval";
	public static final String ACTIONT_START_IQC = "StartIqc";

	private MaterialLotIqcRequestBody body;

}
