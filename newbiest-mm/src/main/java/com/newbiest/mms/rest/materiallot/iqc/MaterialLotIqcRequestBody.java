package com.newbiest.mms.rest.materiallot.iqc;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class MaterialLotIqcRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private MaterialLotAction materialLotAction;

	/**
	 * 接收文件http链接
	 */
	private String urlRemark;
}
