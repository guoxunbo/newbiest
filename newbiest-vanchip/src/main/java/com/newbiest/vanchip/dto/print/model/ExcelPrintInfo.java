package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class ExcelPrintInfo implements Serializable {

    private String actionType;

    private CocPrintInfo cocPrintInfo;

    private PackingListPrintInfo packingListPrintInfo;

    private ShippingListPrintInfo shippingListPrintInfo;

    private PKListPrintInfo pKListPrintInfo;

    private DeliveryPrintInfo deliveryPrintInfo;

    private RSAndScrapInfo rSAndScrapInfo;

}
