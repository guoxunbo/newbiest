package com.newbiest.gc.rest.relayBoxStockIn;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class RelayBoxStockInResponse extends Response {

    private static final long serialVersionUID = 1L;

    private RelayBoxStockInResponseBody body;
}
