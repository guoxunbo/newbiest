package com.newbiest.gc.rest.materiallot;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class GcMaterialLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "BindRelayBox/UnbindRelayBox")
	private String actionType;

	@ApiModelProperty(value = "物料批次")
	private List<MaterialLot> materialLots;

	@ApiModelProperty("中转箱号")
	private String relayBoxId;

	@ApiModelProperty("判定等级")
	private String judgeGrade;

	@ApiModelProperty("判定码")
	private String judgeCode;


}
