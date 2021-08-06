package com.newbiest.vanchip.dto.erp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ErpResponseReturn implements Serializable {

    @ApiModelProperty(value = "成功")
    public final static String SUCCESS_STATUS = "S";

    @ApiModelProperty(value = "失败")
    public final static String FAIL_STATUS = "E";

    @ApiModelProperty(value = "返回的消息")
    private String MESSAGE;

    @ApiModelProperty(value = "状态",example = "S/E")
    private String STATUS;


}
