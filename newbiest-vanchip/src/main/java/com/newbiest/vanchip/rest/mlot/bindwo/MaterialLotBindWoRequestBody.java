package com.newbiest.vanchip.rest.mlot.bindwo;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class MaterialLotBindWoRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作信息", example = "Bind/Unbind/UnbindAndBind")
    private String actionType;

    @ApiModelProperty(value = "物料批次号")
    private List<String> materialLotIdList;

    @ApiModelProperty(value = "UnbindAndBind操作，绑定操作的集合")
    private List<String> materialLotIdExtList;

    @ApiModelProperty(value = "工单号")
    private String workOrderId;

}
