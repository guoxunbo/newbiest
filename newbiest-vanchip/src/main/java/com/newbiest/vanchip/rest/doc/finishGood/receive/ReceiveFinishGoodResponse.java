package com.newbiest.vanchip.rest.doc.finishGood.receive;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ReceiveFinishGoodResponse extends Response {

    private ReceiveFinishGoodResponseBody body;
}
