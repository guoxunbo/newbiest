package com.newbiest.gc.rest.materiallot.update;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class GcMaterialLotUpdateRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "UpdateTreasuryNote/UpdateLocation")
	private String actionType;

	@ApiModelProperty(value = "物料批次")
	private List<MaterialLot> materialLotList;

	@ApiModelProperty("入库备注")
	private String treasuryeNote;

	@ApiModelProperty("保税属性")
	private String location;

	@ApiModelProperty(value = "物料批次号")
	private String materialLotId;

	@ApiModelProperty("扣留/释放原因")
	private String reason;

	@ApiModelProperty("扣留备注")
	private String remarks;

	@ApiModelProperty("系统参数名称")
	private String referenceName;

	@ApiModelProperty("物料批次")
	private MaterialLot materialLot;

	@ApiModelProperty("MRB结论备注")
	private String mrbComments;

	@ApiModelProperty("Hold Type")
	private String holdType;
}
