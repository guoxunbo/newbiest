package com.newbiest.gc.rest.relayBoxStockIn;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class RelayBoxStockInRequest extends Request {

    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_NAME = "GCRelayBoxStockIn";

    public static final String ACTION_QUERY_BOX = "QueryBox";
    public static final String ACTION_QUERY_RELAYBOX = "QueryRelayBox";
    public static final String ACTION_RELAYBOX_STOCK_IN = "RelayBoxStockIn";

    private RelayBoxStockInRequestBody  body;
}
