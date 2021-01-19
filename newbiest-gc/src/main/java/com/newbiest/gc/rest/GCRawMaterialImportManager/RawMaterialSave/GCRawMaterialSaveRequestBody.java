package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class GCRawMaterialSaveRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作类型")
    private String actionType;

    @ApiModelProperty(value = "来料导入类型")
    private String importType;

    @ApiModelProperty(value = "来料信息")
    private List<MaterialLot> materialLotList;

    @ApiModelProperty(value = "发料单据")
    private DocumentLine documentLine;

    @ApiModelProperty(value = "报废原因")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remarks;

}
