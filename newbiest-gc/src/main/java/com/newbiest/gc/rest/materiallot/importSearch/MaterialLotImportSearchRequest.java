package com.newbiest.gc.rest.materiallot.importSearch;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotImportSearchRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCMaterialLotUpdate";

	private MaterialLotImportSearchRequestBody body;

}
