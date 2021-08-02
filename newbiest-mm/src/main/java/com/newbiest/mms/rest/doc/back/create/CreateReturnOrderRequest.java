package com.newbiest.mms.rest.doc.back.create;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CreateReturnOrderRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "CreateReturnOrder";

	@ApiModelProperty(value = "创建仓库退供应商单据")
	public static final String ACTION_TYPE_CREATE_RETURN_MATERIAL_LOT_ORDER = "CreateReturnMaterialLotOrder";

	@ApiModelProperty(value = "创建退货单据")
	public static final String ACTION_TYPE_CREATE_RETURN_GOODS_ORDER = "CreateReturnGoodsOrder";

	private CreateReturnOrderRequestBody body;

}
