package com.newbiest.mms.rest.doc.shippingOrder.save;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ShipmentOrderSaveResponse extends Response {

    private static final long serialVersionUID = 1L;

    private ShipmentOrderSaveResponseBody body;
}
