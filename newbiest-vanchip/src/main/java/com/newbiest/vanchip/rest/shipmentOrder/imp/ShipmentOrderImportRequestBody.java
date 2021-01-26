package com.newbiest.vanchip.rest.shipmentOrder.imp;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShipmentOrderImportRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "导入传递的NBTable")
    private String importTypeNbTable ;
}
