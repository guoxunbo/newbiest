package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;

/**
 * packingList
 */
@Data
public class PackingListBoxPrintInfo implements Serializable{

    /**
     * 模板在进行list赋值时似乎不赞成驼峰命名法
     */

    private Integer ctn_idx;

    private String versionNumber;

    private String controlLot;
    private String contromerLotNo;

    private String ctn_no;
    private String part_number;
    private String reel_code;
    private String lot_no;
    private String qty;
    private String po_no;
    private String dc;
    private String carton_size;
    private String carton_qty;
    private String nw;
    private String gw;

}
