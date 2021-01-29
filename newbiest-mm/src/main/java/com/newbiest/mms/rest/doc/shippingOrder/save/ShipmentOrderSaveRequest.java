package com.newbiest.mms.rest.doc.shippingOrder.save;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "ShipmentOrderPrintRequest")
public class ShipmentOrderSaveRequest extends Request {

    public static final String MESSAGE_NAME = "ShipmentOrder";

    private ShipmentOrderSaveRequestBody body;
}
