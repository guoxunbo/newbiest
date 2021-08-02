package com.newbiest.vanchip.rest.erp.check;

import com.newbiest.base.msg.Request;
import lombok.Data;

@Data
public class ErpCreateCheckOrderRequest extends Request {

    public static final String MESSAGE_NAME = "ErpCreateCheckOrder";

    private ErpCreateCheckOrderRequestBody body;
}
