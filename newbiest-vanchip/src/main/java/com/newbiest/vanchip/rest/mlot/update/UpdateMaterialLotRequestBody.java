package com.newbiest.vanchip.rest.mlot.update;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class UpdateMaterialLotRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作信息", example = "UpdateMLot")
    private String actionType;

    @ApiModelProperty(value = "物料批次")
    private List<MaterialLot> materialLotList;

    @ApiModelProperty(value = "物料批次号")
    private String materialLotId;

    @ApiModelProperty(value = "打印日期")
    private String iclDateValue;
}
