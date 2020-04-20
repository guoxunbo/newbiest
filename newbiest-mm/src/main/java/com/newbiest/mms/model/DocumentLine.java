package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 单据对应的详细信息
 * Created by guoxunbo on 2019-08-29 17:51
 */
@Data
@Table(name = "MMS_DOCUMENT_LINE")
@Entity
public class DocumentLine extends NBUpdatable {

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

    @Column(name="ERP_CREATED", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    protected Date erpCreated;

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

    @Column(name="RESERVED10")
    private String reserved10;
}
