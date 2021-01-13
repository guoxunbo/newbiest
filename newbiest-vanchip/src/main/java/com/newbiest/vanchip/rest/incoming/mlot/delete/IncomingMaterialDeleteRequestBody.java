package com.newbiest.vanchip.rest.incoming.mlot.delete;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class IncomingMaterialDeleteRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料信息")
    private List<MaterialLot> materialLotList;

    @ApiModelProperty(value = "删除备注")
    private String deleteNote;

}