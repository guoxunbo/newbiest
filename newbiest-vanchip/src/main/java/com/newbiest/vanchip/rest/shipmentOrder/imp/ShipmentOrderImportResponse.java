package com.newbiest.vanchip.rest.shipmentOrder.imp;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ShipmentOrderImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private ShipmentOrderImportResponseBody body;
}
