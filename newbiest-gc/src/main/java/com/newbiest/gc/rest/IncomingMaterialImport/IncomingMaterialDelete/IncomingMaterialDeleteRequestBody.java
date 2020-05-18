package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialDelete;

import com.newbiest.gc.model.GCLcdCogDetial;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class IncomingMaterialDeleteRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作类型", example = "DeleteIncomingMLot/DeleteCOGDetial")
    private String actionType;

    @ApiModelProperty(value = "晶圆信息")
    private List<MaterialLotUnit> materialLotUnitList;

    @ApiModelProperty(value = "COG明细")
    private List<GCLcdCogDetial> lcdCogDetialList;

    @ApiModelProperty(value = "删除备注")
    private String deleteNote;

}
