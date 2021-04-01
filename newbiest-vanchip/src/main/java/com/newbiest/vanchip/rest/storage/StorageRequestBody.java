package com.newbiest.vanchip.rest.storage;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.Storage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class StorageRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private String actionType ;

    @ApiModelProperty(value = "库位信息")
    private Storage storage ;

}
