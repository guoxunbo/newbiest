package com.newbiest.vanchip.rest.storage.imp;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StorageImportRequest extends Request {

    public static final String MESSAGE_NAME = "StorageManager";

    private StorageImportRequestBody body;
}
