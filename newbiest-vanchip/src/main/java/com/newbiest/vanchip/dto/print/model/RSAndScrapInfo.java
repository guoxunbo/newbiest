package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RSAndScrapInfo implements Serializable {

    public static final String PRINT_TYPE_RETURN_SUPPLIER = "ReturnMLot";
    public static final String PRINT_TYPE_SCROP_ORDER = "Scrap";

    private String printType;
    private String docId;
    private String docIdBarCode;
    private String created;
    private String rmaNo;
    private String contact;
    private String printDate;
    private String shippingDate;
    private String tel;
    private String address;
    private String logistics;
    private String totalQty;
    private String comment;

    private List<RSAndScrapMLotInfo> rsAndScrapMLotInfos;
}
