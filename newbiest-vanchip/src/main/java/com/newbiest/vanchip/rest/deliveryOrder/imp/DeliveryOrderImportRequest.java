package com.newbiest.vanchip.rest.deliveryOrder.imp;

import com.newbiest.base.msg.Request;
import lombok.Data;

@Data
public class DeliveryOrderImportRequest extends Request {

    public static final String MESSAGE_NAME = "DeliveryOrderImport";

    private DeliveryOrderImportRequestBody body;
}
