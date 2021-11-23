package com.newbiest.gc.rest.check;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class CheckRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 系统存在的物料批次
	 */
	private List<MaterialLot> existMaterialLots;

	/**
	 * 系统不存在的物料批次
	 */
	private List<MaterialLot> errorMaterialLots;


	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty(value="动态表主键")
	private Long tableRrn;

	@ApiModelProperty(value="扫描栏位")
	private String queryLotId;

}
