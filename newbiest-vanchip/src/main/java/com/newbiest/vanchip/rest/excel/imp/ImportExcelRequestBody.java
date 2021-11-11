package com.newbiest.vanchip.rest.excel.imp;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImportExcelRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("动态表主键")
    private String tableRrn;

}
