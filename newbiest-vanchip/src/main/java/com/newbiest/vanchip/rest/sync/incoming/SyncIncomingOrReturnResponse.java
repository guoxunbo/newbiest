package com.newbiest.vanchip.rest.sync.incoming;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class SyncIncomingOrReturnResponse extends Response {

    private static final long serialVersionUID = 1L;

    private SyncIncomingOrReturnResponseBody body;
}
