package com.newbiest.gc.rest.waferUnpack;

import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

/**
 * Created by Youqing Huang 20210915
 */
@Data
public class WaferUnpackResponseBody extends ResponseBody {

	private MaterialLotUnit materialLotUnit;

}
