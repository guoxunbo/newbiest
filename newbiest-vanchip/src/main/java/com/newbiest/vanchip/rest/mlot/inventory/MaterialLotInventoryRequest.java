package com.newbiest.vanchip.rest.mlot.inventory;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotInventoryRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "MaterialLotInvManage";

	public static final String ACTION_GET_MATERIAL_LOT_BY_DOC = "GetMaterialLotByDoc";
	public static final String ACTION_GET_MATERIAL_LOT_BY_DOC_ID = "GetMaterialLotByDocId";

	public static final String ACTION_PICK = "Pick";


	private MaterialLotInventoryRequestBody body;

}
