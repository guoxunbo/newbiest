package com.newbiest.mms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Data
@Table(name = "MMS_DOCUMENT_LINE_HIS")
@Entity
public class DocumentLineHistory extends NBHis {

    @Column(name="DOC_RRN")
    private Long docRrn;

    @Column(name="DOC_ID")
    private String docId;

    @Column(name="MATERIAL_RRN")
    private Long materialRrn;

    @Column(name="MATERIAL_NAME")
    private String materialName;

    @Column(name="QTY")
    private BigDecimal qty;

    @Column(name="HANDLED_QTY")
    private BigDecimal handledQty = BigDecimal.ZERO;

    @Column(name="UN_HANDLE_QTY")
    private BigDecimal unHandledQty = BigDecimal.ZERO;

    @Column(name="RESERVED_QTY")
    private BigDecimal reservedQty = BigDecimal.ZERO;

    @Column(name="UN_RESERVED_QTY")
    private BigDecimal unReservedQty = BigDecimal.ZERO;

    @Column(name="ERP_CREATED", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATE_PATTERN)
    @Getter
    protected Date erpCreated;

    /**
     * 关联 ERP CVOUCHTYPE  单据类型
     */
    @Column(name="DOC_TYPE")
    private String docType;

    /**
     * 关联 ERP CVOUCHNAME  单据名称
     */
    @Column(name="DOC_NAME")
    private String docName;

    /**
     * 关联 ERP CBUSTYPE  单据业务类型
     */
    @Column(name="DOC_BUS_TYPE")
    private String docBusType;

    /**
     * 关联 ERP CSOURCE  单据源
     */
    @Column(name="DOC_SOURCE")
    private String docSource;

    /**
     * 关联 ERP CWHCODE  转入/转出仓库编码
     */
    @Column(name="WAREHOUSE_CODE")
    private String warehouseCode;

    /**
     * 关联 ERP CWHNAME  仓库名称
     */
    @Column(name="WAREHOUSE_NAME")
    private String warehouseName;

    /**
     * 三方交易
     */
    @Column(name="THREE_SIDE_TRANSACTION")
    private String threeSideTransaction;

    /**
     * 快递单号
     */
    @Column(name="EXPRESSS_NUMBER")
    private String expressNumber;

    /**
     * 关联ERP LINE的SEQ主键
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * 关联ERP secondcode
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * 关联ERP grade
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * 关联ERP cfree3
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     * 关联ERP CMAKER
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     * 关联ERP CHANDLER
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     * 关联 ERP OTHER1
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     * 光联erp CUSNAME
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     * 单据类型 GC特殊要求。不显示document主表，只显示line信息。所以所有信息都得带过来
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     * 关联 ERP G_CODE
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     * 关联 ERP G_NAME
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     * 关联 ERP CUSABBNAME
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     * 关联 ERP MEMO
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     * 关联 ERP ITEMCODE
     */
    @Column(name="RESERVED14")
    private String reserved14;

    /**
     * 关联 ERP SHIPADDRESS
     */
    @Column(name="RESERVED15")
    private String reserved15;

    /**
     * 关联 ERP OTHER2
     */
    @Column(name="RESERVED16")
    private String reserved16;

    /**
     * 关联 ERP OTHER3
     */
    @Column(name="RESERVED17")
    private String reserved17;

    /**
     * 关联 ERP OTHER6
     */
    @Column(name="RESERVED18")
    private String reserved18;

    /**
     * 关联 ERP OTHER8
     */
    @Column(name="RESERVED19")
    private String reserved19;

    /**
     * 关联 ERP OTHER9
     */
    @Column(name="RESERVED20")
    private String reserved20;

    /**
     * 关联 ERP OTHER10
     */
    @Column(name="RESERVED21")
    private String reserved21;

    /**
     * 关联 ERP OTHER11
     */
    @Column(name="RESERVED22")
    private String reserved22;

    /**
     * 关联 ERP OTHER12
     */
    @Column(name="RESERVED23")
    private String reserved23;

    /**
     * 关联 ERP OTHER13
     */
    @Column(name="RESERVED24")
    private String reserved24;

    /**
     * 关联 ERP OTHER14
     */
    @Column(name="RESERVED25")
    private String reserved25;

    /**
     * 关联 ERP OTHER15
     */
    @Column(name="RESERVED26")
    private String reserved26;

    @Column(name="RESERVED27")
    private String reserved27;

    @Column(name="RESERVED28")
    private String reserved28;

    @Column(name="RESERVED29")
    private String reserved29;

    @Column(name="RESERVED30")
    private String reserved30;

    @Column(name="RESERVED31")
    private String reserved31;

}