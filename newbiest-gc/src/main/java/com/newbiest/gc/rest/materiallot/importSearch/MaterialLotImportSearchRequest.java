package com.newbiest.gc.rest.materiallot.importSearch;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class MaterialLotImportSearchRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCMaterialLotUpdate";

	public static final String ACTION_TYPE_IMPORT_QUERY_MLOT = "ImportQueryMLot";

	public static final String ACTION_TYPE_IMPORT_QUERY_MLOT_UNIT = "ImportQueryMLotUnit";

	public static final String ACTION_TYPE_RW_IMPORT_QUERY_MLOT = "RwImportQueryMLot";

	private MaterialLotImportSearchRequestBody body;

}
