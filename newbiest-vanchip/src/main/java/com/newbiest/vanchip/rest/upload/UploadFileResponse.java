package com.newbiest.vanchip.rest.upload;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class UploadFileResponse extends Response {

    private static final long serialVersionUID = 1L;

    private UploadFileResponseBody body;
}
