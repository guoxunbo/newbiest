package com.newbiest.gc.rest.waferUnpack;

import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Youqing Huang 20210915
 */
@Data
@ApiModel("具体请求操作信息")
public class WaferUnpackRequestBody extends RequestBody {

	@ApiModelProperty(value = "操作类型", example = "Create/Update等")
	private String actionType;

	@ApiModelProperty(value = "操作的materialLotUnit")
	private List<MaterialLotUnit> materialLotUnits;

}
