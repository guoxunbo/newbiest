package com.newbiest.vanchip.rest.finishGood.reserved;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FinishGoodReservedRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作动作")
    private String actionType ;

    @ApiModelProperty(value = "发货单 单据信息")
    private DocumentLine documentLine;

    @ApiModelProperty(value = "完成品信息")
    private List<MaterialLotAction> materialLotActionList;

    @ApiModelProperty(value = "标准数量")
    private BigDecimal standardQty;
}
