package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class GCRawMaterialSaveRequest extends Request {

    public static final String MESSAGE_NAME = "GCRawMaterialManager";

    public static final String ACTION_TYPE_CREATE = "Create";

    public static final String ACTION_TYPE_RECEIVE = "Receive";

    public static final String ACTION_TYPE_ISSUE = "RawIssue";

    public static final String ACTION_TYPE_SCRAP = "Scrap";

    public static final String ACTION_TYPE_DELETE = "Delete";

    public static final String ACTION_TYPE_QUERY_SPARE_MLOT = "QuerySpareMLot";

    public static final String ACTION_TYPE_GET_SPARE_RAW_MLOT = "GetSpareRawMLot";

    public static final String ACTION_TYPE_GET_SPARE_RWA_OUT_DOC="GetSpareRawOutDoc";

    public static final String ACTION_TYPE_SPARE_RAW_MLOT = "SpareRawMLot";

    public static final String ACTION_TYPE_SPARE_RAW_OUT_DOC = "RawMaterialSpareOutDoc";

    public static final String ACTION_TYPE_QUERY_ISSUE_RAW_MATERIAL_LOT = "QueryIssueRawMaterialLot";

    public static final String ACTION_TYPE_SCRAP_RAW_MATERIAL_SHIP = "ScrapRawMLotShip";

    public static final String ACTION_TYPE_GC_UN_RAW_MATERIAL_SPARE = "GCUnRawMaterialSpare";

    public static final String ACTION_TYPE_MOBILE_ISSUE = "MobileRawIssue";

    public static final String ISSUE_WITH_DOC = "issueWithDoc";

    public static final String ACTION_TYPE_MOBILE_RAW_MATERIAL_OTHER_SHIP = "MobileRawMaterialOtherShip";

    private GCRawMaterialSaveRequestBody body;
}
