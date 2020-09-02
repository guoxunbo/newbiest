package com.newbiest.mms.rest.rawmaterial;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel
public class RawMaterialRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "RawMaterialManage";

	public static final String ACTION_CREATE_PARTS = "CreataParts";

	public static final String ACTION_UPDATE_PARTS = "UpdateParts";

	private RawMaterialRequestBody body;

}
