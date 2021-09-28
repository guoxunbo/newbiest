package com.newbiest.vanchip.rest.erp.order;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ErpCreateOrderRequest extends Request {

    public static final String MESSAGE_NAME = "CreateOrder";

    @ApiModelProperty("创建盘点单")
    public static final String ACTION_TYPE_CREATE_CHECK_ORDER = "createCheckOrder";

    @ApiModelProperty("创建报废单")
    public static final String ACTION_TYPE_CREATE_SCRAP_ORDER = "createScrapOrder";

    @ApiModelProperty("删除报废单")
    public static final String ACTION_TYPE_DELETE_SCRAP_ORDER = "delScrapOrder";

    private ErpCreateOrderRequestBody body;
}
