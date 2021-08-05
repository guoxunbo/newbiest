package com.newbiest.vanchip.rest.erp.order;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class ErpCreateOrderResponse extends Response {

    private ErpCreateOrderResponseBody body;
}
