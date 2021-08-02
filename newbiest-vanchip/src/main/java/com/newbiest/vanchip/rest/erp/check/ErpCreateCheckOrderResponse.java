package com.newbiest.vanchip.rest.erp.check;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ErpCreateCheckOrderResponse extends Response {

    private ErpCreateCheckOrderResponseBody body;
}
