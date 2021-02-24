package com.newbiest.vanchip.rest.deliveryOrder.imp;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class DeliveryOrderImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private DeliveryOrderImportResponseBody body;
}
