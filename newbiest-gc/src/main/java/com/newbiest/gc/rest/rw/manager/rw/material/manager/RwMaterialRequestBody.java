package com.newbiest.gc.rest.rw.manager.rw.material.manager;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class RwMaterialRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "接收/备料等")
	private String actionType;

	@ApiModelProperty(value = "tapeMaterialCode")
	private String tapeMaterialCode;

	@ApiModelProperty(value = "辅料信息")
	private List<MaterialLot> materialLotList;

	@ApiModelProperty(value = "bladeMaterialCode")
	private String bladeMaterialCode;

	@ApiModelProperty(value = "materialLotCode")
	private String materialLotCode;

	@ApiModelProperty(value = "tapeSize")
	private String tapeSize;

}
