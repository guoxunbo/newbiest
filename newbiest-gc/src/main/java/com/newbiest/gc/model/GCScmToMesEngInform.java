package com.newbiest.gc.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by youqingHuang
 */
@Entity
@Table(name = "GC_SCM_TO_MES_ENG_INFORM")
@Data
public class GCScmToMesEngInform extends NBUpdatable {


    /**
     * LOT ID
     */
    @Column(name = "LOT_ID")
    private String lotId;

    /**
     * 产品型号
     */
    @Column(name = "PRODUCT_ID")
    private String productId;

    /**
     * 片号
     */
    @Column(name = "WAFER_ID")
    private String waferId;

    /**
     * hold标识
     */
    @Column(name = "HOLD_FLAG")
    private String holdFlag;

    /**
     * hold备注
     */
    @Column(name = "HOLD_DESC")
    private String holdDesc;

    /**
     * 时间
     */
    @Column(name = "ACTION_TIME")
    private String actionTime;

    /**
     * 预留1
     */
    @Column(name = "OTHER1")
    private String other1;

    /**
     * 预留2
     */
    @Column(name = "OTHER2")
    private String other2;

    /**
     * 预留3
     */
    @Column(name = "OTHER3")
    private String other3;

    /**
     * 预留4
     */
    @Column(name = "OTHER4")
    private String other4;

    /**
     * 预留5
     */
    @Column(name = "OTHER5")
    private String other5;
}
