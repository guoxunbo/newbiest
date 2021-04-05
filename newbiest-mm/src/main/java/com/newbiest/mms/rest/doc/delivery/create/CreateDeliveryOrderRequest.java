package com.newbiest.mms.rest.doc.delivery.create;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class CreateDeliveryOrderRequest extends Request {

    public static final String MESSAGE_NAME = "DeliveryOrder";

    public static final String ACTION_TYPE_CREATE = "createDelivery";
    public static final String ACTION_TYPE_APPROVE = "approveDelivery";

    private CreateDeliveryOrderRequestBody body;
}
