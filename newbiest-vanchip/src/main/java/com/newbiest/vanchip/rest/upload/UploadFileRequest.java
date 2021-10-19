package com.newbiest.vanchip.rest.upload;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UploadFileRequest extends Request {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_NAME = "UploadFileManager";

    @ApiModelProperty(value = "PackingList文件上传")
    public static final String ACTION_UPLOAD_FILE = "uploadFile";

    private UploadFileRequestBody body;
}
