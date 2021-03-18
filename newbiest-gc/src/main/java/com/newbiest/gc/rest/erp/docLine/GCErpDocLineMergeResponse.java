package com.newbiest.gc.rest.erp.docLine;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class GCErpDocLineMergeResponse extends Response {

    private static final long serialVersionUID = 1L;

    private GCErpDocLineMergeResponseBody body;
}
