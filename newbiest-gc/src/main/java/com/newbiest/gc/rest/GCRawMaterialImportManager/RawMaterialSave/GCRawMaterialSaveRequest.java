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

    private GCRawMaterialSaveRequestBody body;
}
