package com.newbiest.gc.scm.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author guoxunbo
 * @date 2/19/21 11:03 AM
 */
@Data
public class TempCpModel implements Serializable {

    private String waferId;

    private String boxId;

    private String waferType;

    private String stockId;

    private String pointId;

    private Date inTime;

    private String secondCode;

    private String location;

    private String vendor;

    private String lotId;

    private String poNo;

    private String woId;

    private String fabDevice;

    private String cartonNo;

    private String invoiceId;

    private String dataValue5;

    private String dataValue6;

    private String dataValue7;

    private String dataValue18;

    private String cstWaferQty;

    private String prodRemarkDesc;

}
