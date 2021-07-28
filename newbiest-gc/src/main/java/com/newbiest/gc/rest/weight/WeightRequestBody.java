package com.newbiest.gc.rest.weight;

import com.newbiest.gc.model.WeightModel;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class WeightRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作类型", example = "Query/Weight")
    private String actionType;

    @ApiModelProperty(value = "物料批次号")
    private String materialLotId;

    @ApiModelProperty(value = "绑定箱重量")
    private List<WeightModel> weightModels;

    @ApiModelProperty(value = "动态表主键")
    private Long tableRrn;

}
