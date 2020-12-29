package com.newbiest.vanchip.rest.IncomingMatLotManager.Save;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialImportSaveRequest extends Request {

    public static final String MESSAGE_NAME = "IncomingMaterialImportManagers";

    private IncomingMaterialImportSaveRequestBody body;
}
