package com.newbiest.gc.scm.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author luoguozhang
 * @date 2/11/22 10:01 AM
 */
@Data
public class TempFtModel implements Serializable {

    public static final String WAFER_SOURCE_1 = "1";
    public static final String WAFER_SOURCE_2 = "2";
    public static final String WAFER_SOURCE_3 = "3";
    public static final String WAFER_SOURCE_11 = "11";
    public static final String WAFER_SOURCE_12 = "12";
    public static final String WAFER_SOURCE_21 = "21";
    public static final String WAFER_SOURCE_31 = "31";
    public static final String WAFER_SOURCE_32 = "32";
    public static final String WAFER_SOURCE_33 = "33";
    public static final String WAFER_SOURCE_34 = "34";
    public static final String WAFER_SOURCE_39 = "39";
    public static final String WAFER_SOURCE_100 = "100";

    public static final String BOX_START_B = "B";
    public static final String BOX_START_SBB = "SBB";

    public static final List<String> WAFER_SOURCE_LIST_35 = Lists.newArrayList(WAFER_SOURCE_1, WAFER_SOURCE_3, WAFER_SOURCE_33, WAFER_SOURCE_34);
    public static final List<String> WAFER_SOURCE_LIST_4 = Lists.newArrayList(WAFER_SOURCE_2, WAFER_SOURCE_11, WAFER_SOURCE_12, WAFER_SOURCE_21, WAFER_SOURCE_31, WAFER_SOURCE_32, WAFER_SOURCE_39);

    private String waferId;

    private String boxId;

    private String grade;

    private String stockId;

    private String pointId;

    private String waferNum;

    private Date inTime;

    private String productId;

    private String waferSource;

    private String secondCode;

    private String location;

    private String vendor;

    private String passNum;

    private String ngNum;

    private String lotId;

    private String poNo;

    private String woId;

    private String fabDevice;

    private String cartonNo;

    private String packDevice;

    private String yield;

    private String remark;

    private String invoiceId;

    private String packLotId;

    private String dataValue3;

    private String dataValue4;

    private String dataValue5;

    private String dataValue8;

    private String dataValue12;

    private String dataValue13;

    private String dataValue14;

    private String dataValue16;

    private String dataValue19;

    private String dataValue20;

    private String cstId;

    private String saleRemarkDesc;

    private String prodRemarkDesc;

    private String holdDesc;

    private String materialId;

    private String vqrId;

    private String bqrId;

    private String dataValue24;

    private String dataValue25;

    private String dataValue29;

}
