package com.newbiest.vanchip.rest.storage.imp;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class StorageImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private StorageImportResponseBody body;
}
