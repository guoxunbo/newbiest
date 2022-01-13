package com.newbiest.gc.rest.IncomingMaterialImport.HNWarehouseImport;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class HNWarehouseImportRequestBody   extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "导入类型")
    private String importType;

    @ApiModelProperty(value = "导入文件名称")
    private String fileName;

    @ApiModelProperty(value = "操作类型")
    private String actionType;

    @ApiModelProperty(value = "来料信息")
    private List<MaterialLot> materialLotList;

}
