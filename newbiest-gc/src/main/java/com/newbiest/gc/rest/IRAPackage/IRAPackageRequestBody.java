package com.newbiest.gc.rest.IRAPackage;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class IRAPackageRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	public static final String IRA_PACKAGE = "IRAPackCase";
	public static final String IRA_UN_PACKAGE = "IRAUnPackage";

	@ApiModelProperty(value = "物料批次操作")
	private List<MaterialLotAction> materialLotActions;

	@ApiModelProperty(value = "包装规则")
	private String packageType;

}
