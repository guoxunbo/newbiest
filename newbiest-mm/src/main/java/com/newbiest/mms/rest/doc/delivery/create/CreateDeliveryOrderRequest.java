package com.newbiest.mms.rest.doc.delivery.create;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CreateDeliveryOrderRequest extends Request {

    public static final String MESSAGE_NAME = "DeliveryOrder";

    public static final String ACTION_TYPE_CREATE = "createDelivery";
    public static final String ACTION_TYPE_APPROVE = "approveDelivery";

    @ApiModelProperty(value = "创建byReelCode的发货单")
    public static final String ACTION_TYPE_CREATE_BY_REEL_DELIVERY = "createByReelDelivery";


    private CreateDeliveryOrderRequestBody body;
}
