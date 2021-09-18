package com.newbiest.gc.rest.waferUnpack;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Youqing Huang 20210915
 */
@Data
@ApiModel
public class WaferUnpackRequest extends Request {

	public static final String ACTION_WAFER_UNPACK = "WaferUnpack";

	private WaferUnpackRequestBody body;

}
