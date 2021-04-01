package com.newbiest.vanchip.rest.mlot.weight;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class MaterialLotWeightRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料号")
    private String materialLotId;

    @ApiModelProperty(value = "毛重")
    private String grossWeight;

    @ApiModelProperty(value = "外箱尺寸")
    private String cartonSize;

}
