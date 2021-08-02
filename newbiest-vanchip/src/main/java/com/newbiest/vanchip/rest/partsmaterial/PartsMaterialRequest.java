package com.newbiest.vanchip.rest.partsmaterial;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class PartsMaterialRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "PartsMaterialManage";

	public static final String ACTION_MERGE_PARTS = "MergeParts";

	public static final String ACTION_IMPORT_PARTS = "ImportParts";

	public static final String ACTION_RECEIVE_PARTS = "ReceiveParts";

	private PartsMaterialRequestBody body;

}
