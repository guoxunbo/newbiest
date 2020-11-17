package com.newbiest.gc.rest.mesSaveMLotHis;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class MesSaveMLotHisRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "SaveMLotHis/SaveMLotUnitHis")
	private String actionType;

	@ApiModelProperty(value = "物料批次")
	private List<MaterialLot> materialLots;

	@ApiModelProperty("晶圆信息")
	private List<MaterialLotUnit> materialLotUnits;

	@ApiModelProperty("事务号")
	private String transId;
}
