package com.newbiest.vanchip.rest.incoming.mlot.imp;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class IncomingMaterialImportRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "导入传递的NBTable")
    private String importTypeNbTable ;

}
