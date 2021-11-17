package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PackingListPrintInfo implements Serializable {

    /**
     * 客户名称
     */
    private String customerName;

    private String packingListType;

    private String shipTo;
    private String shipAdd;

    private String documentLineId;
    private String shipDate;

    private String tel;
    private String attn;
    private String cNO;

    private String totalQty;
    private String totalNW;
    private String totalGW;

    List<PackingListBoxPrintInfo> packingListBoxPrintInfos;
}
