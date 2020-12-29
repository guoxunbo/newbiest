package com.newbiest.vanchip.rest.IncomingMatLotManager.Receive;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class IncomingMaterialImportReceiveResponse extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialImportReceiveResponseBody body;
}
