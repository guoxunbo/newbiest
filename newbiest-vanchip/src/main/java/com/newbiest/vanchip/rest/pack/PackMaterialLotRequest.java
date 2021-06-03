package com.newbiest.vanchip.rest.pack;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class PackMaterialLotRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "PackMaterialLot";

	public static final String ACTION_PACKAGE_MLOT = "PackageMLot";
	public static final String ACTION_PRINT_PACKAGE_MLOT = "PrintPackageMLot";


	private PackMaterialLotRequestBody body;

}
