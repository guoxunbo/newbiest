package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;

/**
 * coc
 */
@Data
public class CocPrintInfo implements Serializable {

    private String customer;

    /**
     * 客户名称
     */
    private String customerName;

    private String documentLineId;

    private String partNumber;

    private String poNumber;

    private String soNumber;

    private String invoiceNumber;

    private String quantity;

    private String shippingDate;
}
