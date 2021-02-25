package com.newbiest.vanchip.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name="MES_PACKED_LOT_DETAIL")
public class MesPackedLotDetail implements Serializable {

    @Id
    @Column(name = "PACKED_LOT_DETAIL_RRN")
    private Long packedLotDetailRrn;

    @Column(name = "PACKED_LOT_RRN")
    private Long packedLotRrn;

    @Column(name = "LOT_ID")
    private String lotId;

    /**
     * 产品型号
     */
    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "LEVEL_TWO_CODE")
    private String levelTwoCode;

    @Column(name = "GRADE")
    private String grade;

    @Column(name = "WAFER_ID")
    private String waferId;

    /**
     * 包装数量
     */
    @Column(name = "PACKAGE_QTY")
    private String packageQty;

    /**
     * 工单
     */
    @Column(name = "WORKORDER_ID")
    private String workorderId;


    @Column(name = "TRANS_RRN")
    private String transRrn;


}
