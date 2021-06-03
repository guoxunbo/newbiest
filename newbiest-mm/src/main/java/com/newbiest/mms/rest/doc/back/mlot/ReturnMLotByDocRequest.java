package com.newbiest.mms.rest.doc.back.mlot;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ReturnMLotByDocRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "ReturnMLotByDoc";

	@ApiModelProperty(value = "产线退料")
	public static final String ACTION_TYPE_RETURN_MLOT = "ReturnMLot";
	public static final String ACTION_TYPE_GET_MATERIAL_LOT = "GetMaterialLot";

	@ApiModelProperty(value = "仓库退料")
	public static final String ACTION_TYPE_RETURN_MATERIAL_LOT = "ReturnMaterialLot";

	@ApiModelProperty(value = "仓库退料")
	public static final String ACTION_TYPE_RETURN_GOODS = "ReturnGoods";

	private ReturnMLotByDocRequestBody body;

}
