package com.newbiest.gc.rest.materiallot.update;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GcMaterialLotUpdateRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "GCMaterialLotUpdate";

	public static final String ACTION_UPDATE_TREASURY_NOTE = "UpdateTreasuryNote";
	public static final String ACTION_UPDATE_LOCATION = "UpdateLocation";
	public static final String ACTION_QUERY = "Query";
	public static final String ACTION_HOLD = "HoldMLot";
	public static final String ACTION_RELEASE = "ReleaseMLot";
	public static final String ACTION_TYPE_QUERY_REFERENCE_LIST = "QueryReferenceList";
	public static final String ACTION_TYPE_UPDATE_LOT_INFO = "UpdateLotInfo";
	public static final String ACTION_TYPE_UPDATE_MRB_COMMENTS = "UpdateMRBComments";
	public static final String ACTION_TYPE_SAVE_PACKAGE_SHIPI_HIS = "SaveShipHis";

	private GcMaterialLotUpdateRequestBody body;

}
