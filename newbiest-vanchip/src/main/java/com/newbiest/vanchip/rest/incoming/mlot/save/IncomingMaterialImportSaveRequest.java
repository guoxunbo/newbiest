package com.newbiest.vanchip.rest.incoming.mlot.save;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialImportSaveRequest extends Request {

    public static final String MESSAGE_NAME = "IncomingMaterialImportManagers";

    @ApiParam(value = "materialLotId生成规则")
    public static final String GENERATOR_MATERIAL_LOT_ID = "CreateMaterialLotId";

    public static final String MLOT_SAVE = "MLotSave";
    public static final String MATERIAL_SAVE = "MaterialSave";

    private IncomingMaterialImportSaveRequestBody body;
}
