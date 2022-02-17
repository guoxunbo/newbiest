package com.newbiest.gc.rest.relayBoxStockIn;

import com.newbiest.gc.model.RelayBoxStockInModel;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class RelayBoxStockInRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作类型", example = "Query/ChangeStorage")
    private String actionType;

    @ApiModelProperty(value = "物料批次号")
    private String materialLotId;

    @ApiModelProperty(value = "中转箱号")
    private String relayBoxId;

    @ApiModelProperty(value = "表单主键")
    private Long tableRrn;

    @ApiModelProperty(value = "更换库位号")
    private List<RelayBoxStockInModel> relayBoxStockInModels;
}
