package com.newbiest.mms.rest.iqc;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotJudgeAction;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel("具体请求操作信息")
public class IqcCheckRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型")
	private String actionType;

	@ApiModelProperty("物料批次判定动作")
	private MaterialLotJudgeAction materialLotJudgeAction;

	@ApiModelProperty("检查项Rrn")
	private String checkSheetRrn;
}
