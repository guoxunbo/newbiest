package com.newbiest.base.ui.rest.table;

import com.newbiest.base.ui.model.NBTable;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class TableRequestBody extends RequestBody {

    @ApiModelProperty(example = "GetByAuthority/GetData")
    private String actionType;

    @ApiModelProperty(example = "菜单的主键")
    private Long authorityRrn;

    @ApiModelProperty(example = "动态表")
    private NBTable table;
}
