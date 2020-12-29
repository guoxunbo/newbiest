package com.newbiest.vanchip.rest.incoming.mlot.imp;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("具体请求操作信息")
public class IncomingMaterialImportRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

}
