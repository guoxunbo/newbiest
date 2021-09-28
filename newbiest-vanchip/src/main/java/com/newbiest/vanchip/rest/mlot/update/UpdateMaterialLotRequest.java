package com.newbiest.vanchip.rest.mlot.update;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "MaterialLotUpdateRequest")
public class UpdateMaterialLotRequest extends Request {

    public static final String MESSAGE_NAME = "MaterialLotUpdateMLot";

    public static final String ACTION_TYPE_UPDATE_SO = "updateSo";
    public static final String ACTION_TYPE_UPDATE_ICL_DATE = "updateIclDate";

    private UpdateMaterialLotRequestBody body;
}
