package com.newbiest.vanchip.rest.incoming.mlot.receive;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class IncomingMaterialImportReceiveRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "来料接收信息")
    private List<MaterialLot> materialLots;

    @ApiModelProperty(value = "类型")
    private String actionType;

    @ApiModelProperty(value = "单据号")
    private String docId;

}
