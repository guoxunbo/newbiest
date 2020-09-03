package com.newbiest.mms.rest.partsMaterial;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by guoZhang Luo on 2019/9/3.
 */
@Data
@ApiModel
public class PartsMaterialRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "PartsMaterialManage";

	public static final String ACTION_CREATE_PARTS = "CreateParts";

	public static final String ACTION_UPDATE_PARTS = "UpdateParts";

	private PartsMaterialRequestBody body;

}
