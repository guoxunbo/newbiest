package com.newbiest.vanchip.rest.storage;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class StorageResponse extends Response {

    private static final long serialVersionUID = 1L;

    private StorageResponseBody body;
}
