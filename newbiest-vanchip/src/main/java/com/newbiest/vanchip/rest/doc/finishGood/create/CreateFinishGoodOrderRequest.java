package com.newbiest.vanchip.rest.doc.finishGood.create;

import com.newbiest.base.msg.Request;
import lombok.Data;

@Data
public class CreateFinishGoodOrderRequest extends Request {

    public static final String MESSAGE_NAME = "FinishGoodManager";

    private CreateFinishGoodOrderRequestBody body;
}
