package com.newbiest.vanchip.rest.doc.finishGood.receive;

import com.newbiest.base.msg.Request;
import lombok.Data;

@Data
public class ReceiveFinishGoodRequest extends Request {

    public static final String MESSAGE_NAME = "FinishGoodManager";

    private ReceiveFinishGoodRequestBody body;
}
