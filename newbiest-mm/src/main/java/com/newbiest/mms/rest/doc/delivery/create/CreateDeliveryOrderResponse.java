package com.newbiest.mms.rest.doc.delivery.create;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class CreateDeliveryOrderResponse extends Response {

    private static final long serialVersionUID = 1L;

    private CreateDeliveryOrderResponseBody body;

}
