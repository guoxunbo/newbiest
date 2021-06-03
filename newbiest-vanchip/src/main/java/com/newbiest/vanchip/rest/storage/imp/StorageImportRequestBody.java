package com.newbiest.vanchip.rest.storage.imp;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class StorageImportRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private String objectRrn;
}
