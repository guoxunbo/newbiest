package com.newbiest.vanchip.rest.mlot.weight;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "UpdateMaterialLotRequest")
public class MaterialLotWeightRequest extends Request {

    public static final String MESSAGE_NAME = "MaterialLotWeight";

    private MaterialLotWeightRequestBody body;
}
