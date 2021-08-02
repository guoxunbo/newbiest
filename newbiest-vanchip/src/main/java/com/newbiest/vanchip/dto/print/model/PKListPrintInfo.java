package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 配料单
 */
@Data
public class PKListPrintInfo implements Serializable {

    private String lineId;

    private String pKId;

    private String partVersion;

    private String partNumber;

    private String customerCode;

    private List<PKListMLotPrintInfo> pKListMLotPrintInfos;
}
