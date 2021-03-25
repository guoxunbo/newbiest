package com.newbiest.gc.rest.rw.manager;

import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class RwMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Query/StockIn")
	private String actionType;

	@ApiModelProperty(value = "物料批次号")
	private List<MaterialLot> materialLotList;

	@ApiModelProperty(value = "完成品")
	private List<MesPackedLot> mesPackedLots;

	@ApiModelProperty(value = "打印标签")
	private String printLabel;
}
