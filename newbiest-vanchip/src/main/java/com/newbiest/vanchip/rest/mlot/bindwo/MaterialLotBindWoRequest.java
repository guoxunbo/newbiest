package com.newbiest.vanchip.rest.mlot.bindwo;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "UpdateMaterialLotRequest")
public class MaterialLotBindWoRequest extends Request {

    public static final String MESSAGE_NAME = "MaterialLotBindWo";

    public static final String ACTION_TYPE_BIND = "Bind";
    public static final String ACTION_TYPE_UNBIND = "Unbind";
    public static final String ACTION_TYPE_UNBIND_AND_BIND = "UnbindAndBind";

    private MaterialLotBindWoRequestBody body;
}
