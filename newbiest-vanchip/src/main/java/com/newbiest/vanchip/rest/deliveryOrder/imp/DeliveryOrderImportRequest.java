package com.newbiest.vanchip.rest.deliveryOrder.imp;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "ShipmentOrderPrintRequest")
public class DeliveryOrderImportRequest extends Request {

    public static final String MESSAGE_NAME = "DeliveryOrderImport";

    private DeliveryOrderImportRequestBody body;
}
