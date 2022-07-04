package com.newbiest.gc.rest.excelExport;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class ExportExcelRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "导出箱号信息")
    private List<MaterialLot> materialLotList;

    @ApiModelProperty(value = "导出晶圆信息")
    private List<MaterialLotUnit> materialLotUnitList;

    @ApiModelProperty(value = "表单名称")
    private String tableName;

    @ApiModelProperty(value = "操作类型")
    private String actionType;
}
