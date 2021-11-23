package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
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
    private List<DocumentLine> documentLineList;

    @ApiModelProperty(value = "报废原因")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "单据主键")
    private Long docLineRrn;

    @ApiModelProperty(value = "table主键")
    private Long tableRrn ;

    @ApiModelProperty(value = "原材料批号/箱号")
    private String queryLotId ;

    @ApiModelProperty(value = "单据信息")
    private DocumentLine documentLine ;

    @ApiModelProperty(value = "发料绑定单据")
    private String issueWithDoc;

    @ApiModelProperty(value = "手持端发料单据日期")
    private String erpTime;

    @ApiModelProperty(value = "数量")
    private BigDecimal pickQty;

}
