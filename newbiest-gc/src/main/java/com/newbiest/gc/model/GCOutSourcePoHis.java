package com.newbiest.gc.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by guozhangLuo
 */
@Entity
@Table(name="GC_OUTSOURCE_PO_HIS")
@Data
public class GCOutSourcePoHis extends NBHis {

    public static final String TRANS_TYPE_STOCK_OUT_TAG = "StockOutTag";
    public static final String TRANS_TYPE_UNSTOCK_OUT_TAG = "UnStockOutTag";

    /**
     * PO号
     */
    @Column(name="PO_ID")
    private String poId;

    /**
     * 厂商名称
     */
    @Column(name="SUPPLIER_NAME")
    private String supplierName;

    /**
     * 产品型号
     */
    @Column(name="MATERIAL_NAME")
    private String materialName;

    /**
     * 导入型号
     */
    @Column(name="IMPORT_TYPE")
    private String importType;

    /**
     * PO总量
     */
    @Column(name="PO_CURRENT_QTY")
    private BigDecimal poCurrentQty = BigDecimal.ZERO;

    /**
     * 已使用数量
     */
    @Column(name="HANDLED_QTY")
    private BigDecimal handledQty = BigDecimal.ZERO;

    /**
     * 未使用数量
     */
    @Column(name="UN_HANDLE_QTY")
    private BigDecimal unHandledQty = BigDecimal.ZERO;

    /**
     * 备注信息
     */
    @Column(name="REMARKS")
    private String remarks;

}
