package com.newbiest.vanchip.rest.storage;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class StorageRequest extends Request {


    public static final String MESSAGE_NAME = "StorageManager";

    public static final String ACTION_SAVE_STORAGE_INFO = "saveStorageInfo";

    private StorageRequestBody body;
}
