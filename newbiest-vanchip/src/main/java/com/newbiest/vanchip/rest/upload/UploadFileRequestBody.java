package com.newbiest.vanchip.rest.upload;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class UploadFileRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private String actionType ;

    @ApiModelProperty("类全名")
    private String modelClass;

}
