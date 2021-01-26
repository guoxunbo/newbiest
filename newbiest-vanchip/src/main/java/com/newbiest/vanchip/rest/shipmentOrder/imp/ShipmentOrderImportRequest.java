package com.newbiest.vanchip.rest.shipmentOrder.imp;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "ShipmentOrderPrintRequest")
public class ShipmentOrderImportRequest extends Request {

    public static final String MESSAGE_NAME = "ShipmentOrder";

    private ShipmentOrderImportRequestBody body;
}
