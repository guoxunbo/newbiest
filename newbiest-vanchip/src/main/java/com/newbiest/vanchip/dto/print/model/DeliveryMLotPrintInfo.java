package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 发货单
 */
@Data
public class DeliveryMLotPrintInfo implements Serializable {

    private String partNumber;
    private String partVersion;
    private String grade;
    private String mrb;
    private String qty;
    private String remark;
}
