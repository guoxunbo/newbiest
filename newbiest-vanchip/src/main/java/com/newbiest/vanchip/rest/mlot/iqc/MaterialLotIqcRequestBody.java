package com.newbiest.vanchip.rest.mlot.iqc;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MLotCheckSheetLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class MaterialLotIqcRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	private List<String> materialLotIds;

	private List<MaterialLotAction> materialLotActions;

	/**
	 * 接收文件http链接
	 */
	private String urlRemark;

	@ApiModelProperty("检查项")
	private List<MLotCheckSheetLine> checkSheetLineList;
}
