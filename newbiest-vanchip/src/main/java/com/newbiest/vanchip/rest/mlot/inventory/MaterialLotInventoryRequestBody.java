package com.newbiest.vanchip.rest.mlot.inventory;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.Document;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class MaterialLotInventoryRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "操作类型", example = "Receive/StockIn等")
	private String actionType;

	@ApiModelProperty(value = "单据")
	private Document document;

	@ApiModelProperty(value = "单据号")
	private String documentId;

	@ApiModelProperty("物料操作，包含了数量仓库等")
	private List<MaterialLotAction> materialLotActions;

}
