package com.newbiest.gc.rest.IncomingMaterialImport.IncomingMaterialSave;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class IncomingMaterialSaveRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "来料信息")
    private List<? extends NBUpdatable> dataList;

    @ApiModelProperty(value = "来料信息")
    private List<MaterialLot> materialLotList;

    @ApiModelProperty(value = "晶圆信息")
    private List<MaterialLotUnit> materialLotUnitList;

    @ApiModelProperty(value = "仓库号")
    private String warehouseId;

    @ApiModelProperty(value = "导入类型")
    private String importType;
}
