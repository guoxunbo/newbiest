package com.newbiest.vanchip.rest.mlot.update;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MaterialLotUpdateRequest")
public class UpdateMaterialLotRequest extends Request {

    public static final String MESSAGE_NAME = "MaterialLotUpdateMLot";

    @ApiModelProperty("修改销售订单信息")
    public static final String ACTION_TYPE_UPDATE_SO = "updateSo";
    @ApiModelProperty("修改生产日期")
    public static final String ACTION_TYPE_UPDATE_ICL_DATE = "updateIclDate";
    @ApiModelProperty("修改/添加RMA号码")
    public static final String ACTION_TYPE_UPDATE_RMA_NO = "updateRmaNo";

    private UpdateMaterialLotRequestBody body;
}
